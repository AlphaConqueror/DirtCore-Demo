/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.phys;

import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;

public class Vec3 {

    public static final Vec3 ZERO = new Vec3(0.0D, 0.0D, 0.0D);
    public final double x;
    public final double y;
    public final double z;

    private Vec3(final double pX, final double pY, final double pZ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
    }

    public static Vec3 from(final double pX, final double pY, final double pZ) {
        return new Vec3(pX, pY, pZ);
    }

    /**
     * Copies the coordinates of an int vector exactly.
     */
    public static Vec3 atLowerCornerOf(final Vec3i pToCopy) {
        return new Vec3((double) pToCopy.getX(), (double) pToCopy.getY(), (double) pToCopy.getZ());
    }

    public static Vec3 atLowerCornerWithOffset(final Vec3i pToCopy, final double pOffsetX,
            final double pOffsetY, final double pOffsetZ) {
        return new Vec3((double) pToCopy.getX() + pOffsetX, (double) pToCopy.getY() + pOffsetY,
                (double) pToCopy.getZ() + pOffsetZ);
    }

    /**
     * Copies the coordinates of an Int vector and centers them.
     */
    public static Vec3 atCenterOf(final Vec3i pToCopy) {
        return atLowerCornerWithOffset(pToCopy, 0.5D, 0.5D, 0.5D);
    }

    /**
     * Copies the coordinates of an int vector and centers them horizontally (x and z)
     */
    public static Vec3 atBottomCenterOf(final Vec3i pToCopy) {
        return atLowerCornerWithOffset(pToCopy, 0.5D, 0.0D, 0.5D);
    }

    /**
     * Copies the coordinates of an int vector and centers them horizontally and applies a
     * vertical offset.
     */
    public static Vec3 upFromBottomCenterOf(final Vec3i pToCopy, final double pVerticalOffset) {
        return atLowerCornerWithOffset(pToCopy, 0.5D, pVerticalOffset, 0.5D);
    }

    /**
     * Returns a {@link Vec3} from the given pitch and yaw degrees.
     */
    public static Vec3 directionFromRotation(final float pPitch, final float pYaw) {
        final float f = Mth.cos(-pYaw * ((float) Math.PI / 180F) - (float) Math.PI);
        final float f1 = Mth.sin(-pYaw * ((float) Math.PI / 180F) - (float) Math.PI);
        final float f2 = -Mth.cos(-pPitch * ((float) Math.PI / 180F));
        final float f3 = Mth.sin(-pPitch * ((float) Math.PI / 180F));
        return new Vec3(f1 * f2, f3, f * f2);
    }

    /**
     * Returns a new vector with the result of the specified vector minus this.
     */
    public Vec3 vectorTo(final Vec3 pVec) {
        return new Vec3(pVec.x - this.x, pVec.y - this.y, pVec.z - this.z);
    }

    /**
     * Normalizes the vector to a length of 1 (except if it is the zero vector)
     */
    public Vec3 normalize() {
        final double d0 = Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
        return d0 < 1.0E-4D ? ZERO : new Vec3(this.x / d0, this.y / d0, this.z / d0);
    }

    public double dot(final Vec3 pVec) {
        return this.x * pVec.x + this.y * pVec.y + this.z * pVec.z;
    }

    /**
     * Returns a new vector with the result of this vector x the specified vector.
     */
    public Vec3 cross(final Vec3 pVec) {
        return new Vec3(this.y * pVec.z - this.z * pVec.y, this.z * pVec.x - this.x * pVec.z,
                this.x * pVec.y - this.y * pVec.x);
    }

    public Vec3 subtract(final Vec3 pVec) {
        return this.subtract(pVec.x, pVec.y, pVec.z);
    }

    public Vec3 subtract(final double pX, final double pY, final double pZ) {
        return this.add(-pX, -pY, -pZ);
    }

    public Vec3 add(final Vec3 pVec) {
        return this.add(pVec.x, pVec.y, pVec.z);
    }

    /**
     * Adds the specified x,y,z vector components to this vector and returns the resulting vector
     * . Does not change this
     * vector.
     */
    public Vec3 add(final double pX, final double pY, final double pZ) {
        return new Vec3(this.x + pX, this.y + pY, this.z + pZ);
    }

    /**
     * Checks if a position is within a certain distance of the coordinates.
     */
    public boolean closerThan(final Vec3 pVec, final double pDistance) {
        return this.distanceToSqr(pVec.x(), pVec.y(), pVec.z()) < pDistance * pDistance;
    }

    /**
     * Euclidean distance between this and the specified vector, returned as double.
     */
    public double distanceTo(final Vec3 pVec) {
        final double d0 = pVec.x - this.x;
        final double d1 = pVec.y - this.y;
        final double d2 = pVec.z - this.z;
        return Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }

    /**
     * The square of the Euclidean distance between this and the specified vector.
     */
    public double distanceToSqr(final Vec3 pVec) {
        final double d0 = pVec.x - this.x;
        final double d1 = pVec.y - this.y;
        final double d2 = pVec.z - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double distanceToSqr(final double pX, final double pY, final double pZ) {
        final double d0 = pX - this.x;
        final double d1 = pY - this.y;
        final double d2 = pZ - this.z;
        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public Vec3 scale(final double pFactor) {
        return this.multiply(pFactor, pFactor, pFactor);
    }

    public Vec3 reverse() {
        return this.scale(-1.0D);
    }

    public Vec3 multiply(final Vec3 pVec) {
        return this.multiply(pVec.x, pVec.y, pVec.z);
    }

    public Vec3 multiply(final double pFactorX, final double pFactorY, final double pFactorZ) {
        return new Vec3(this.x * pFactorX, this.y * pFactorY, this.z * pFactorZ);
    }

    /**
     * Returns the length of the vector.
     */
    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public double lengthSqr() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    public double horizontalDistance() {
        return Math.sqrt(this.x * this.x + this.z * this.z);
    }

    public double horizontalDistanceSqr() {
        return this.x * this.x + this.z * this.z;
    }

    @Override
    public int hashCode() {
        long j = Double.doubleToLongBits(this.x);
        int i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.y);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.z);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    @Override
    public boolean equals(final Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof Vec3)) {
            return false;
        } else {
            final Vec3 vec3 = (Vec3) pOther;
            if (Double.compare(vec3.x, this.x) != 0) {
                return false;
            } else if (Double.compare(vec3.y, this.y) != 0) {
                return false;
            } else {
                return Double.compare(vec3.z, this.z) == 0;
            }
        }
    }

    @Override
    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

    /**
     * Lerps between this vector and the given vector.
     *
     * @see Mth#lerp(double, double, double)
     */
    public Vec3 lerp(final Vec3 pTo, final double pDelta) {
        return new Vec3(Mth.lerp(pDelta, this.x, pTo.x), Mth.lerp(pDelta, this.y, pTo.y),
                Mth.lerp(pDelta, this.z, pTo.z));
    }

    public Vec3 xRot(final float pPitch) {
        final float f = Mth.cos(pPitch);
        final float f1 = Mth.sin(pPitch);
        final double d1 = this.y * (double) f + this.z * (double) f1;
        final double d2 = this.z * (double) f - this.y * (double) f1;
        return new Vec3(this.x, d1, d2);
    }

    public Vec3 yRot(final float pYaw) {
        final float f = Mth.cos(pYaw);
        final float f1 = Mth.sin(pYaw);
        final double d0 = this.x * (double) f + this.z * (double) f1;
        final double d2 = this.z * (double) f - this.x * (double) f1;
        return new Vec3(d0, this.y, d2);
    }

    public Vec3 zRot(final float pRoll) {
        final float f = Mth.cos(pRoll);
        final float f1 = Mth.sin(pRoll);
        final double d0 = this.x * (double) f + this.y * (double) f1;
        final double d1 = this.y * (double) f - this.x * (double) f1;
        return new Vec3(d0, d1, this.z);
    }

    public final double x() {
        return this.x;
    }

    public final double y() {
        return this.y;
    }

    public final double z() {
        return this.z;
    }
}
