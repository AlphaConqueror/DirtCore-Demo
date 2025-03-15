/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.phys;

import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;

public class AABB {

    public final double minX;
    public final double minY;
    public final double minZ;
    public final double maxX;
    public final double maxY;
    public final double maxZ;

    public AABB(final double pX1, final double pY1, final double pZ1, final double pX2,
            final double pY2, final double pZ2) {
        this.minX = Math.min(pX1, pX2);
        this.minY = Math.min(pY1, pY2);
        this.minZ = Math.min(pZ1, pZ2);
        this.maxX = Math.max(pX1, pX2);
        this.maxY = Math.max(pY1, pY2);
        this.maxZ = Math.max(pZ1, pZ2);
    }

    public AABB(final BlockPos pPos) {
        this(pPos.getX(), pPos.getY(), pPos.getZ(), pPos.getX() + 1, pPos.getY() + 1,
                pPos.getZ() + 1);
    }

    public AABB(final BlockPos pStart, final BlockPos pEnd) {
        this(pStart.getX(), pStart.getY(), pStart.getZ(), pEnd.getX(), pEnd.getY(), pEnd.getZ());
    }

    public AABB(final Vec3 pStart, final Vec3 pEnd) {
        this(pStart.x, pStart.y, pStart.z, pEnd.x, pEnd.y, pEnd.z);
    }

    public static AABB unitCubeFromLowerCorner(final Vec3 pVector) {
        return new AABB(pVector.x, pVector.y, pVector.z, pVector.x + 1.0D, pVector.y + 1.0D,
                pVector.z + 1.0D);
    }

    public static AABB ofSize(final Vec3 pCenter, final double pXSize, final double pYSize,
            final double pZSize) {
        return new AABB(pCenter.x - pXSize / 2.0D, pCenter.y - pYSize / 2.0D,
                pCenter.z - pZSize / 2.0D, pCenter.x + pXSize / 2.0D, pCenter.y + pYSize / 2.0D,
                pCenter.z + pZSize / 2.0D);
    }

    public AABB setMinX(final double pMinX) {
        return new AABB(pMinX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public AABB setMinY(final double pMinY) {
        return new AABB(this.minX, pMinY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    public AABB setMinZ(final double pMinZ) {
        return new AABB(this.minX, this.minY, pMinZ, this.maxX, this.maxY, this.maxZ);
    }

    public AABB setMaxX(final double pMaxX) {
        return new AABB(this.minX, this.minY, this.minZ, pMaxX, this.maxY, this.maxZ);
    }

    public AABB setMaxY(final double pMaxY) {
        return new AABB(this.minX, this.minY, this.minZ, this.maxX, pMaxY, this.maxZ);
    }

    public AABB setMaxZ(final double pMaxZ) {
        return new AABB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, pMaxZ);
    }

    @Override
    public int hashCode() {
        long i = Double.doubleToLongBits(this.minX);
        int j = (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minY);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.minZ);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxX);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxY);
        j = 31 * j + (int) (i ^ i >>> 32);
        i = Double.doubleToLongBits(this.maxZ);
        return 31 * j + (int) (i ^ i >>> 32);
    }

    @Override
    public boolean equals(final Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof AABB)) {
            return false;
        } else {
            final AABB aabb = (AABB) pOther;
            if (Double.compare(aabb.minX, this.minX) != 0) {
                return false;
            } else if (Double.compare(aabb.minY, this.minY) != 0) {
                return false;
            } else if (Double.compare(aabb.minZ, this.minZ) != 0) {
                return false;
            } else if (Double.compare(aabb.maxX, this.maxX) != 0) {
                return false;
            } else if (Double.compare(aabb.maxY, this.maxY) != 0) {
                return false;
            } else {
                return Double.compare(aabb.maxZ, this.maxZ) == 0;
            }
        }
    }

    @Override
    public String toString() {
        return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX
                + ", " + this.maxY + ", " + this.maxZ + "]";
    }

    public AABB contract(final double pX, final double pY, final double pZ) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;
        if (pX < 0.0D) {
            d0 -= pX;
        } else if (pX > 0.0D) {
            d3 -= pX;
        }

        if (pY < 0.0D) {
            d1 -= pY;
        } else if (pY > 0.0D) {
            d4 -= pY;
        }

        if (pZ < 0.0D) {
            d2 -= pZ;
        } else if (pZ > 0.0D) {
            d5 -= pZ;
        }

        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    public AABB expandTowards(final Vec3 pVector) {
        return this.expandTowards(pVector.x, pVector.y, pVector.z);
    }

    public AABB expandTowards(final double pX, final double pY, final double pZ) {
        double d0 = this.minX;
        double d1 = this.minY;
        double d2 = this.minZ;
        double d3 = this.maxX;
        double d4 = this.maxY;
        double d5 = this.maxZ;
        if (pX < 0.0D) {
            d0 += pX;
        } else if (pX > 0.0D) {
            d3 += pX;
        }

        if (pY < 0.0D) {
            d1 += pY;
        } else if (pY > 0.0D) {
            d4 += pY;
        }

        if (pZ < 0.0D) {
            d2 += pZ;
        } else if (pZ > 0.0D) {
            d5 += pZ;
        }

        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    public AABB inflate(final double pX, final double pY, final double pZ) {
        final double d0 = this.minX - pX;
        final double d1 = this.minY - pY;
        final double d2 = this.minZ - pZ;
        final double d3 = this.maxX + pX;
        final double d4 = this.maxY + pY;
        final double d5 = this.maxZ + pZ;
        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    public AABB inflate(final double pValue) {
        return this.inflate(pValue, pValue, pValue);
    }

    public AABB intersect(final AABB pOther) {
        final double d0 = Math.max(this.minX, pOther.minX);
        final double d1 = Math.max(this.minY, pOther.minY);
        final double d2 = Math.max(this.minZ, pOther.minZ);
        final double d3 = Math.min(this.maxX, pOther.maxX);
        final double d4 = Math.min(this.maxY, pOther.maxY);
        final double d5 = Math.min(this.maxZ, pOther.maxZ);
        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    public AABB minmax(final AABB pOther) {
        final double d0 = Math.min(this.minX, pOther.minX);
        final double d1 = Math.min(this.minY, pOther.minY);
        final double d2 = Math.min(this.minZ, pOther.minZ);
        final double d3 = Math.max(this.maxX, pOther.maxX);
        final double d4 = Math.max(this.maxY, pOther.maxY);
        final double d5 = Math.max(this.maxZ, pOther.maxZ);
        return new AABB(d0, d1, d2, d3, d4, d5);
    }

    /**
     * Offsets the current bounding box by the specified amount.
     */
    public AABB move(final double pX, final double pY, final double pZ) {
        return new AABB(this.minX + pX, this.minY + pY, this.minZ + pZ, this.maxX + pX,
                this.maxY + pY, this.maxZ + pZ);
    }

    public AABB move(final BlockPos pPos) {
        return new AABB(this.minX + (double) pPos.getX(), this.minY + (double) pPos.getY(),
                this.minZ + (double) pPos.getZ(), this.maxX + (double) pPos.getX(),
                this.maxY + (double) pPos.getY(), this.maxZ + (double) pPos.getZ());
    }

    public AABB move(final Vec3 pVec) {
        return this.move(pVec.x, pVec.y, pVec.z);
    }

    /**
     * Checks if the bounding box intersects with another.
     */
    public boolean intersects(final AABB pOther) {
        return this.intersects(pOther.minX, pOther.minY, pOther.minZ, pOther.maxX, pOther.maxY,
                pOther.maxZ);
    }

    public boolean intersects(final double pX1, final double pY1, final double pZ1,
            final double pX2, final double pY2, final double pZ2) {
        return this.minX < pX2 && this.maxX > pX1 && this.minY < pY2 && this.maxY > pY1
                && this.minZ < pZ2 && this.maxZ > pZ1;
    }

    public boolean intersects(final Vec3 pMin, final Vec3 pMax) {
        return this.intersects(Math.min(pMin.x, pMax.x), Math.min(pMin.y, pMax.y),
                Math.min(pMin.z, pMax.z), Math.max(pMin.x, pMax.x), Math.max(pMin.y, pMax.y),
                Math.max(pMin.z, pMax.z));
    }

    /**
     * Returns if the supplied Vec3D is completely inside the bounding box
     */
    public boolean contains(final Vec3 pVec) {
        return this.contains(pVec.x, pVec.y, pVec.z);
    }

    public boolean contains(final double pX, final double pY, final double pZ) {
        return pX >= this.minX && pX < this.maxX && pY >= this.minY && pY < this.maxY
                && pZ >= this.minZ && pZ < this.maxZ;
    }

    /**
     * Returns the average length of the edges of the bounding box.
     */
    public double getSize() {
        final double d0 = this.getXsize();
        final double d1 = this.getYsize();
        final double d2 = this.getZsize();
        return (d0 + d1 + d2) / 3.0D;
    }

    public double getXsize() {
        return this.maxX - this.minX;
    }

    public double getYsize() {
        return this.maxY - this.minY;
    }

    public double getZsize() {
        return this.maxZ - this.minZ;
    }

    public AABB deflate(final double pX, final double pY, final double pZ) {
        return this.inflate(-pX, -pY, -pZ);
    }

    public AABB deflate(final double pValue) {
        return this.inflate(-pValue);
    }

    public double distanceToSqr(final Vec3 pVec) {
        final double d0 = Math.max(Math.max(this.minX - pVec.x, pVec.x - this.maxX), 0.0D);
        final double d1 = Math.max(Math.max(this.minY - pVec.y, pVec.y - this.maxY), 0.0D);
        final double d2 = Math.max(Math.max(this.minZ - pVec.z, pVec.z - this.maxZ), 0.0D);
        return Mth.lengthSquared(d0, d1, d2);
    }

    public boolean hasNaN() {
        return Double.isNaN(this.minX) || Double.isNaN(this.minY) || Double.isNaN(this.minZ)
                || Double.isNaN(this.maxX) || Double.isNaN(this.maxY) || Double.isNaN(this.maxZ);
    }

    public Vec3 getCenter() {
        return Vec3.from(Mth.lerp(0.5D, this.minX, this.maxX), Mth.lerp(0.5D, this.minY, this.maxY),
                Mth.lerp(0.5D, this.minZ, this.maxZ));
    }
}
