/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model.minecraft.phys;

import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;

public class Vec2 {

    public static final Vec2 ZERO = new Vec2(0.0F, 0.0F);
    public static final Vec2 ONE = new Vec2(1.0F, 1.0F);
    public static final Vec2 UNIT_X = new Vec2(1.0F, 0.0F);
    public static final Vec2 NEG_UNIT_X = new Vec2(-1.0F, 0.0F);
    public static final Vec2 UNIT_Y = new Vec2(0.0F, 1.0F);
    public static final Vec2 NEG_UNIT_Y = new Vec2(0.0F, -1.0F);
    public static final Vec2 MAX = new Vec2(Float.MAX_VALUE, Float.MAX_VALUE);
    public static final Vec2 MIN = new Vec2(Float.MIN_VALUE, Float.MIN_VALUE);
    public final float x;
    public final float y;

    private Vec2(final float pX, final float pY) {
        this.x = pX;
        this.y = pY;
    }

    public static Vec2 from(final float pX, final float pY) {
        return new Vec2(pX, pY);
    }

    public Vec2 scale(final float pFactor) {
        return new Vec2(this.x * pFactor, this.y * pFactor);
    }

    public float dot(final Vec2 pOther) {
        return this.x * pOther.x + this.y * pOther.y;
    }

    public Vec2 add(final Vec2 pOther) {
        return new Vec2(this.x + pOther.x, this.y + pOther.y);
    }

    public Vec2 add(final float pValue) {
        return new Vec2(this.x + pValue, this.y + pValue);
    }

    public boolean equals(final Vec2 pOther) {
        return this.x == pOther.x && this.y == pOther.y;
    }

    public Vec2 normalized() {
        final float f = Mth.sqrt(this.x * this.x + this.y * this.y);
        return f < 1.0E-4F ? ZERO : new Vec2(this.x / f, this.y / f);
    }

    public float length() {
        return Mth.sqrt(this.x * this.x + this.y * this.y);
    }

    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    public float distanceToSqr(final Vec2 pOther) {
        final float f = pOther.x - this.x;
        final float f1 = pOther.y - this.y;
        return f * f + f1 * f1;
    }

    public Vec2 negated() {
        return new Vec2(-this.x, -this.y);
    }
}
