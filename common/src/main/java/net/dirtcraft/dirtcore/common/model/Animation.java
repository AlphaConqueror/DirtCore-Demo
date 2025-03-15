/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.model;

/**
 * Represents a particle animation.
 */
public enum Animation {

    /**
     * Displayed when the reward from CrateOpenGUI is given.
     */
    CRATE_REWARD(30, 0.1, 0.1, 0.1, 0.1);

    private final int particleCount;
    private final double xOffset;
    private final double yOffset;
    private final double zOffset;
    private final double speed;

    Animation(final int particleCount, final double xOffset, final double yOffset,
            final double zOffset, final double speed) {
        this.particleCount = particleCount;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.speed = speed;
    }

    public int getParticleCount() {
        return this.particleCount;
    }

    public double getXOffset() {
        return this.xOffset;
    }

    public double getYOffset() {
        return this.yOffset;
    }

    public double getZOffset() {
        return this.zOffset;
    }

    public double getSpeed() {
        return this.speed;
    }
}
