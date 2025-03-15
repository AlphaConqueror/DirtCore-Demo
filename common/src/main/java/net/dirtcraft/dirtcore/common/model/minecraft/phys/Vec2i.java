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

import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Vec2i implements Comparable<Vec2i> {

    public static final Vec2i ZERO = new Vec2i(0, 0);
    public static final Vec2i ONE = new Vec2i(1, 1);
    public static final Vec2i UNIT_X = new Vec2i(1, 0);
    public static final Vec2i NEG_UNIT_X = new Vec2i(-1, 0);
    public static final Vec2i UNIT_Y = new Vec2i(0, 1);
    public static final Vec2i NEG_UNIT_Y = new Vec2i(0, -1);
    public static final Vec2i MAX = new Vec2i(Integer.MAX_VALUE, Integer.MAX_VALUE);
    public static final Vec2i MIN = new Vec2i(Integer.MIN_VALUE, Integer.MIN_VALUE);

    public int x;
    public int y;

    private Vec2i(final int pX, final int pY) {
        this.x = pX;
        this.y = pY;
    }

    public static Vec2i from(final int pX, final int pY) {
        return new Vec2i(pX, pY);
    }

    public int getX() {
        return this.x;
    }

    protected Vec2i setX(final int pX) {
        this.x = pX;
        return this;
    }

    @Override
    public int compareTo(@NotNull final Vec2i o) {
        final int compareX = Integer.compare(this.x, o.x);

        if (compareX == 0) {
            return 0;
        }

        return Integer.compare(this.y, o.y);
    }

    public int getY() {
        return this.y;
    }

    protected Vec2i setY(final int pY) {
        this.y = pY;
        return this;
    }

    public Vec2i offset(final int pDx, final int pDy) {
        return pDx == 0 && pDy == 0 ? this : new Vec2i(this.getX() + pDx, this.getY() + pDy);
    }

    public Vec2i offset(final Vec2i pVector) {
        return this.offset(pVector.getX(), pVector.getY());
    }

    public Vec2i subtract(final Vec2i pVector) {
        return this.offset(-pVector.getX(), -pVector.getY());
    }

    public Vec2i multiply(final int pScalar) {
        if (pScalar == 1) {
            return this;
        } else {
            return pScalar == 0 ? ZERO : new Vec2i(this.getX() * pScalar, this.getY() * pScalar);
        }
    }

    public boolean closerThan(final Vec2i pVector, final double pDistance) {
        return this.distSqr(pVector) < Mth.square(pDistance);
    }

    /**
     * Calculate squared distance to the given Vector
     */
    public double distSqr(final Vec2i pVector) {
        return this.distToLowCornerSqr(pVector.getX(), pVector.getY());
    }

    public double distToCenterSqr(final double pX, final double pY, final double pZ) {
        final double d0 = (double) this.getX() + 0.5D - pX;
        final double d1 = (double) this.getY() + 0.5D - pY;
        return d0 * d0 + d1 * d1;
    }

    public double distToLowCornerSqr(final double pX, final double pY) {
        final double d0 = (double) this.getX() - pX;
        final double d1 = (double) this.getY() - pY;
        return d0 * d0 + d1 * d1;
    }

    public int distManhattan(final Vec2i pVector) {
        final float f = (float) Math.abs(pVector.getX() - this.getX());
        final float f1 = (float) Math.abs(pVector.getY() - this.getY());
        return (int) (f + f1);
    }

    public String toShortString() {
        return this.getX() + ", " + this.getY();
    }
}
