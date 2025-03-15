/*
 * MIT License
 *
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.phys;

import com.google.common.collect.AbstractIterator;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;

public class BlockPos extends Vec3i {

    public static final BlockPos ZERO = new BlockPos(0, 0, 0);
    private static final int PACKED_X_LENGTH =
            1 + Mth.log2(Mth.smallestEncompassingPowerOfTwo(30000000));
    private static final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
    public static final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
    private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
    private static final int Z_OFFSET = PACKED_Y_LENGTH;
    private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;
    private static final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
    private static final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
    private static final int Y_OFFSET = 0;

    private BlockPos(final int pX, final int pY, final int pZ) {
        super(pX, pY, pZ);
    }

    public static BlockPos of(final int x, final int y, final int z) {
        return new BlockPos(x, y, z);
    }

    public static BlockPos from(final Vec3i vec3i) {
        return new BlockPos(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public static long offset(final long pPos, final int pDx, final int pDy, final int pDz) {
        return asLong(getX(pPos) + pDx, getY(pPos) + pDy, getZ(pPos) + pDz);
    }

    public static int getX(final long pPackedPos) {
        return (int) (pPackedPos << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
    }

    public static int getY(final long pPackedPos) {
        return (int) (pPackedPos << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
    }

    public static int getZ(final long pPackedPos) {
        return (int) (pPackedPos << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
    }

    public static BlockPos of(final long pPackedPos) {
        return new BlockPos(getX(pPackedPos), getY(pPackedPos), getZ(pPackedPos));
    }

    public static BlockPos containing(final double pX, final double pY, final double pZ) {
        return new BlockPos(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
    }

    public static BlockPos containing(final Vec3 vec3) {
        return containing(vec3.x, vec3.y, vec3.z);
    }

    public static long asLong(final int pX, final int pY, final int pZ) {
        long i = 0L;
        i |= ((long) pX & PACKED_X_MASK) << X_OFFSET;
        i |= ((long) pY & PACKED_Y_MASK);
        return i | ((long) pZ & PACKED_Z_MASK) << Z_OFFSET;
    }

    public static long getFlatIndex(final long pPackedPos) {
        return pPackedPos & -16L;
    }

    public static Iterable<BlockPos> withinManhattan(final BlockPos pPos, final int pXSize,
            final int pYSize, final int pZSize) {
        final int i = pXSize + pYSize + pZSize;
        final int j = pPos.getX();
        final int k = pPos.getY();
        final int l = pPos.getZ();
        return () -> {
            return new AbstractIterator<BlockPos>() {
                private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
                private int currentDepth;
                private int maxX;
                private int maxY;
                private int x;
                private int y;
                private boolean zMirror;

                @Override
                protected BlockPos computeNext() {
                    if (this.zMirror) {
                        this.zMirror = false;
                        this.cursor.setZ(l - (this.cursor.getZ() - l));
                        return this.cursor;
                    } else {
                        BlockPos blockpos;
                        for (blockpos = null; blockpos == null; ++this.y) {
                            if (this.y > this.maxY) {
                                ++this.x;
                                if (this.x > this.maxX) {
                                    ++this.currentDepth;
                                    if (this.currentDepth > i) {
                                        return this.endOfData();
                                    }

                                    this.maxX = Math.min(pXSize, this.currentDepth);
                                    this.x = -this.maxX;
                                }

                                this.maxY = Math.min(pYSize, this.currentDepth - Math.abs(this.x));
                                this.y = -this.maxY;
                            }

                            final int i1 = this.x;
                            final int j1 = this.y;
                            final int k1 = this.currentDepth - Math.abs(i1) - Math.abs(j1);
                            if (k1 <= pZSize) {
                                this.zMirror = k1 != 0;
                                blockpos = this.cursor.set(j + i1, k + j1, l + k1);
                            }
                        }

                        return blockpos;
                    }
                }
            };
        };
    }

    public static Optional<BlockPos> findClosestMatch(final BlockPos pPos, final int pWidth,
            final int pHeight, final Predicate<BlockPos> pPosFilter) {
        for (final BlockPos blockpos : withinManhattan(pPos, pWidth, pHeight, pWidth)) {
            if (pPosFilter.test(blockpos)) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    /**
     * Returns a stream of positions in a box shape, ordered by closest to furthest. Returns by
     * definition the given
     * position as first element in the stream.
     */
    public static Stream<BlockPos> withinManhattanStream(final BlockPos pPos, final int pXSize,
            final int pYSize, final int pZSize) {
        return StreamSupport.stream(withinManhattan(pPos, pXSize, pYSize, pZSize).spliterator(),
                false);
    }

    public static Iterable<BlockPos> betweenClosed(final BlockPos pFirstPos,
            final BlockPos pSecondPos) {
        return betweenClosed(Math.min(pFirstPos.getX(), pSecondPos.getX()),
                Math.min(pFirstPos.getY(), pSecondPos.getY()),
                Math.min(pFirstPos.getZ(), pSecondPos.getZ()),
                Math.max(pFirstPos.getX(), pSecondPos.getX()),
                Math.max(pFirstPos.getY(), pSecondPos.getY()),
                Math.max(pFirstPos.getZ(), pSecondPos.getZ()));
    }

    public static Stream<BlockPos> betweenClosedStream(final BlockPos pFirstPos,
            final BlockPos pSecondPos) {
        return StreamSupport.stream(betweenClosed(pFirstPos, pSecondPos).spliterator(), false);
    }

    public static Stream<BlockPos> betweenClosedStream(final int pMinX, final int pMinY,
            final int pMinZ, final int pMaxX, final int pMaxY, final int pMaxZ) {
        return StreamSupport.stream(
                betweenClosed(pMinX, pMinY, pMinZ, pMaxX, pMaxY, pMaxZ).spliterator(), false);
    }

    public static Iterable<BlockPos> betweenClosed(final int pX1, final int pY1, final int pZ1,
            final int pX2, final int pY2, final int pZ2) {
        final int i = pX2 - pX1 + 1;
        final int j = pY2 - pY1 + 1;
        final int k = pZ2 - pZ1 + 1;
        final int l = i * j * k;
        return () -> {
            return new AbstractIterator<BlockPos>() {
                private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
                private int index;

                @Override
                protected BlockPos computeNext() {
                    if (this.index == l) {
                        return this.endOfData();
                    } else {
                        final int i1 = this.index % i;
                        final int j1 = this.index / i;
                        final int k1 = j1 % j;
                        final int l1 = j1 / j;
                        ++this.index;
                        return this.cursor.set(pX1 + i1, pY1 + k1, pZ1 + l1);
                    }
                }
            };
        };
    }

    public long asLong() {
        return asLong(this.getX(), this.getY(), this.getZ());
    }

    @Override
    public BlockPos offset(final int pDx, final int pDy, final int pDz) {
        return pDx == 0 && pDy == 0 && pDz == 0 ? this
                : new BlockPos(this.getX() + pDx, this.getY() + pDy, this.getZ() + pDz);
    }

    @Override
    public BlockPos offset(final Vec3i pVector) {
        return this.offset(pVector.getX(), pVector.getY(), pVector.getZ());
    }

    @Override
    public BlockPos subtract(final Vec3i pVector) {
        return this.offset(-pVector.getX(), -pVector.getY(), -pVector.getZ());
    }

    @Override
    public BlockPos multiply(final int pScalar) {
        if (pScalar == 1) {
            return this;
        } else {
            return pScalar == 0 ? ZERO : new BlockPos(this.getX() * pScalar, this.getY() * pScalar,
                    this.getZ() * pScalar);
        }
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    @Override
    public BlockPos cross(final Vec3i pVector) {
        return new BlockPos(this.getY() * pVector.getZ() - this.getZ() * pVector.getY(),
                this.getZ() * pVector.getX() - this.getX() * pVector.getZ(),
                this.getX() * pVector.getY() - this.getY() * pVector.getX());
    }

    public Vec3 getCenter() {
        return Vec3.atCenterOf(this);
    }

    public BlockPos atY(final int pY) {
        return new BlockPos(this.getX(), pY, this.getZ());
    }

    /**
     * Returns a version of this BlockPos that is guaranteed to be immutable.
     *
     * <p>When storing a BlockPos given to you for an extended period of time, make sure you
     * use this in case the value is changed internally.</p>
     */
    public BlockPos immutable() {
        return this;
    }

    public BlockPos.MutableBlockPos mutable() {
        return new BlockPos.MutableBlockPos(this.getX(), this.getY(), this.getZ());
    }

    public static class MutableBlockPos extends BlockPos {

        public MutableBlockPos() {
            this(0, 0, 0);
        }

        public MutableBlockPos(final int pX, final int pY, final int pZ) {
            super(pX, pY, pZ);
        }

        public MutableBlockPos(final double pX, final double pY, final double pZ) {
            this(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
        }

        @Override
        public BlockPos offset(final int pDx, final int pDy, final int pDz) {
            return super.offset(pDx, pDy, pDz).immutable();
        }

        @Override
        public BlockPos multiply(final int pScalar) {
            return super.multiply(pScalar).immutable();
        }

        /**
         * Returns a version of this BlockPos that is guaranteed to be immutable.
         *
         * <p>When storing a BlockPos given to you for an extended period of time, make sure you
         * use this in case the value is changed internally.</p>
         */
        @Override
        public BlockPos immutable() {
            return BlockPos.from(this);
        }

        public BlockPos.MutableBlockPos set(final int pX, final int pY, final int pZ) {
            this.setX(pX);
            this.setY(pY);
            this.setZ(pZ);
            return this;
        }

        public BlockPos.MutableBlockPos set(final double pX, final double pY, final double pZ) {
            return this.set(Mth.floor(pX), Mth.floor(pY), Mth.floor(pZ));
        }

        public BlockPos.MutableBlockPos set(final Vec3i pVector) {
            return this.set(pVector.getX(), pVector.getY(), pVector.getZ());
        }

        public BlockPos.MutableBlockPos set(final long pPackedPos) {
            return this.set(getX(pPackedPos), getY(pPackedPos), getZ(pPackedPos));
        }

        public BlockPos.MutableBlockPos setWithOffset(final Vec3i pVector, final int pOffsetX,
                final int pOffsetY, final int pOffsetZ) {
            return this.set(pVector.getX() + pOffsetX, pVector.getY() + pOffsetY,
                    pVector.getZ() + pOffsetZ);
        }

        public BlockPos.MutableBlockPos setWithOffset(final Vec3i pPos, final Vec3i pOffset) {
            return this.set(pPos.getX() + pOffset.getX(), pPos.getY() + pOffset.getY(),
                    pPos.getZ() + pOffset.getZ());
        }

        public BlockPos.MutableBlockPos move(final int pX, final int pY, final int pZ) {
            return this.set(this.getX() + pX, this.getY() + pY, this.getZ() + pZ);
        }

        public BlockPos.MutableBlockPos move(final Vec3i pOffset) {
            return this.set(this.getX() + pOffset.getX(), this.getY() + pOffset.getY(),
                    this.getZ() + pOffset.getZ());
        }

        @Override
        public BlockPos.MutableBlockPos setX(final int pX) {
            super.setX(pX);
            return this;
        }

        @Override
        public BlockPos.MutableBlockPos setY(final int pY) {
            super.setY(pY);
            return this;
        }

        @Override
        public BlockPos.MutableBlockPos setZ(final int pZ) {
            super.setZ(pZ);
            return this;
        }
    }
}
