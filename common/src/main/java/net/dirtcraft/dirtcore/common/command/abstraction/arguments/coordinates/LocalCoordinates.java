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

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates;

import java.util.Objects;
import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.BlockPos;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;
import net.dirtcraft.dirtcore.common.model.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LocalCoordinates implements Coordinates {

    public static final char PREFIX_LOCAL_COORDINATE = '^';
    private final double left;
    private final double up;
    private final double forwards;

    public LocalCoordinates(final double pLeft, final double pUp, final double pForwards) {
        this.left = pLeft;
        this.up = pUp;
        this.forwards = pForwards;
    }

    public static LocalCoordinates parse(final StringReader pReader) throws CommandSyntaxException {
        final int i = pReader.getCursor();
        final double d0 = readDouble(pReader, i);
        if (pReader.canRead() && pReader.peek() == ' ') {
            pReader.skip();
            final double d1 = readDouble(pReader, i);
            if (pReader.canRead() && pReader.peek() == ' ') {
                pReader.skip();
                final double d2 = readDouble(pReader, i);
                return new LocalCoordinates(d0, d1, d2);
            } else {
                pReader.setCursor(i);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
            }
        } else {
            pReader.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
        }
    }

    private static double readDouble(final StringReader pReader,
            final int pStart) throws CommandSyntaxException {
        if (!pReader.canRead()) {
            throw WorldCoordinate.ERROR_EXPECTED_DOUBLE.createWithContext(pReader);
        } else if (pReader.peek() != PREFIX_LOCAL_COORDINATE) {
            pReader.setCursor(pStart);
            throw Vec3Argument.ERROR_MIXED_TYPE.createWithContext(pReader);
        } else {
            pReader.skip();
            return pReader.canRead() && pReader.peek() != ' ' ? pReader.readDouble() : 0.0D;
        }
    }

    @NonNull
    @Override
    public Vec3 getPosition(@NonNull final Sender sender) {
        final Vec2 vec2 = sender.getRotation();
        final Vec3 vec3 = sender.getPosition();
        final float f = Mth.cos((vec2.y + 90.0F) * ((float) Math.PI / 180F));
        final float f1 = Mth.sin((vec2.y + 90.0F) * ((float) Math.PI / 180F));
        final float f2 = Mth.cos(-vec2.x * ((float) Math.PI / 180F));
        final float f3 = Mth.sin(-vec2.x * ((float) Math.PI / 180F));
        final float f4 = Mth.cos((-vec2.x + 90.0F) * ((float) Math.PI / 180F));
        final float f5 = Mth.sin((-vec2.x + 90.0F) * ((float) Math.PI / 180F));
        final Vec3 vec31 = Vec3.from(f * f2, f3, f1 * f2);
        final Vec3 vec32 = Vec3.from(f * f4, f5, f1 * f4);
        final Vec3 vec33 = vec31.cross(vec32).scale(-1.0D);
        final double d0 = vec31.x * this.forwards + vec32.x * this.up + vec33.x * this.left;
        final double d1 = vec31.y * this.forwards + vec32.y * this.up + vec33.y * this.left;
        final double d2 = vec31.z * this.forwards + vec32.z * this.up + vec33.z * this.left;
        return Vec3.from(vec3.x + d0, vec3.y + d1, vec3.z + d2);
    }

    @NonNull
    @Override
    public Vec2 getRotation(@NonNull final Sender sender) {
        return Vec2.ZERO;
    }

    @Override
    public @NonNull Vec2i getChunkPos(@NonNull final Sender sender) {
        final BlockPos blockPos = this.getBlockPos(sender);
        return Vec2i.from(World.blockToChunkCoordinate(blockPos.getX()),
                World.blockToChunkCoordinate(blockPos.getZ()));
    }

    @Override
    public boolean isXRelative() {
        return true;
    }

    @Override
    public boolean isYRelative() {
        return true;
    }

    @Override
    public boolean isZRelative() {
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.left, this.up, this.forwards);
    }

    @Override
    public boolean equals(final Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof LocalCoordinates)) {
            return false;
        } else {
            final LocalCoordinates localcoordinates = (LocalCoordinates) pOther;
            return this.left == localcoordinates.left && this.up == localcoordinates.up
                    && this.forwards == localcoordinates.forwards;
        }
    }
}
