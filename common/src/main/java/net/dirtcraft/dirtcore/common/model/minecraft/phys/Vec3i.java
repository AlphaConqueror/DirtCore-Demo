/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.phys;

import com.google.common.base.MoreObjects;
import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;

public class Vec3i implements Comparable<Vec3i> {

    public static final Vec3i ZERO = new Vec3i(0, 0, 0);
    private int x;
    private int y;
    private int z;

    public Vec3i(final int pX, final int pY, final int pZ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public boolean equals(final Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof Vec3i)) {
            return false;
        } else {
            final Vec3i vec3i = (Vec3i) pOther;
            if (this.getX() != vec3i.getX()) {
                return false;
            } else if (this.getY() != vec3i.getY()) {
                return false;
            } else {
                return this.getZ() == vec3i.getZ();
            }
        }
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("x", this.getX())
                .add("y", this.getY())
                .add("z", this.getZ()).toString();
    }

    @Override
    public int compareTo(final Vec3i pOther) {
        if (this.getY() == pOther.getY()) {
            return this.getZ() == pOther.getZ() ? this.getX() - pOther.getX()
                    : this.getZ() - pOther.getZ();
        } else {
            return this.getY() - pOther.getY();
        }
    }

    public int getX() {
        return this.x;
    }

    protected Vec3i setX(final int pX) {
        this.x = pX;
        return this;
    }

    public int getY() {
        return this.y;
    }

    protected Vec3i setY(final int pY) {
        this.y = pY;
        return this;
    }

    public int getZ() {
        return this.z;
    }

    protected Vec3i setZ(final int pZ) {
        this.z = pZ;
        return this;
    }

    public Vec3i offset(final int pDx, final int pDy, final int pDz) {
        return pDx == 0 && pDy == 0 && pDz == 0 ? this
                : new Vec3i(this.getX() + pDx, this.getY() + pDy, this.getZ() + pDz);
    }

    public Vec3i offset(final Vec3i pVector) {
        return this.offset(pVector.getX(), pVector.getY(), pVector.getZ());
    }

    public Vec3i subtract(final Vec3i pVector) {
        return this.offset(-pVector.getX(), -pVector.getY(), -pVector.getZ());
    }

    public Vec3i multiply(final int pScalar) {
        if (pScalar == 1) {
            return this;
        } else {
            return pScalar == 0 ? ZERO : new Vec3i(this.getX() * pScalar, this.getY() * pScalar,
                    this.getZ() * pScalar);
        }
    }

    /**
     * Calculate the cross product of this and the given Vector
     */
    public Vec3i cross(final Vec3i pVector) {
        return new Vec3i(this.getY() * pVector.getZ() - this.getZ() * pVector.getY(),
                this.getZ() * pVector.getX() - this.getX() * pVector.getZ(),
                this.getX() * pVector.getY() - this.getY() * pVector.getX());
    }

    public boolean closerThan(final Vec3i pVector, final double pDistance) {
        return this.distSqr(pVector) < Mth.square(pDistance);
    }

    /**
     * Calculate squared distance to the given Vector
     */
    public double distSqr(final Vec3i pVector) {
        return this.distToLowCornerSqr((double) pVector.getX(), (double) pVector.getY(),
                (double) pVector.getZ());
    }

    public double distToCenterSqr(final double pX, final double pY, final double pZ) {
        final double d0 = (double) this.getX() + 0.5D - pX;
        final double d1 = (double) this.getY() + 0.5D - pY;
        final double d2 = (double) this.getZ() + 0.5D - pZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distToLowCornerSqr(final double pX, final double pY, final double pZ) {
        final double d0 = (double) this.getX() - pX;
        final double d1 = (double) this.getY() - pY;
        final double d2 = (double) this.getZ() - pZ;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public int distManhattan(final Vec3i pVector) {
        final float f = (float) Math.abs(pVector.getX() - this.getX());
        final float f1 = (float) Math.abs(pVector.getY() - this.getY());
        final float f2 = (float) Math.abs(pVector.getZ() - this.getZ());
        return (int) (f + f1 + f2);
    }

    public String toShortString() {
        return this.getX() + ", " + this.getY() + ", " + this.getZ();
    }
}
