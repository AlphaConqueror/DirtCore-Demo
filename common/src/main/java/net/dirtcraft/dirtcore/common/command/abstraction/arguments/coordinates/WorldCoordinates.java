/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.command.abstraction.arguments.coordinates;

import net.dirtcraft.dirtcore.common.command.abstraction.StringReader;
import net.dirtcraft.dirtcore.common.command.abstraction.exceptions.CommandSyntaxException;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec2i;
import net.dirtcraft.dirtcore.common.model.minecraft.phys.Vec3;
import net.dirtcraft.dirtcore.common.model.minecraft.util.Mth;
import net.dirtcraft.dirtcore.common.model.Sender;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WorldCoordinates implements Coordinates {

    private final WorldCoordinate x;
    private final WorldCoordinate y;
    private final WorldCoordinate z;

    public WorldCoordinates(final WorldCoordinate pX, final WorldCoordinate pY,
            final WorldCoordinate pZ) {
        this.x = pX;
        this.y = pY;
        this.z = pZ;
    }

    public static WorldCoordinates parseInt(
            final StringReader pReader) throws CommandSyntaxException {
        final int i = pReader.getCursor();
        final WorldCoordinate worldcoordinate = WorldCoordinate.parseIntForBlock(pReader);

        if (pReader.canRead() && pReader.peek() == ' ') {
            pReader.skip();

            final WorldCoordinate worldCoordinate = WorldCoordinate.parseIntForBlock(pReader);

            if (pReader.canRead() && pReader.peek() == ' ') {
                pReader.skip();

                final WorldCoordinate worldCoordinate1 = WorldCoordinate.parseIntForBlock(pReader);
                return new WorldCoordinates(worldcoordinate, worldCoordinate, worldCoordinate1);
            } else {
                pReader.setCursor(i);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
            }
        } else {
            pReader.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
        }
    }

    public static WorldCoordinates parseDouble(final StringReader pReader,
            final boolean pCenterCorrect) throws CommandSyntaxException {
        final int i = pReader.getCursor();
        final WorldCoordinate worldcoordinate =
                WorldCoordinate.parseDouble(pReader, pCenterCorrect);
        if (pReader.canRead() && pReader.peek() == ' ') {
            pReader.skip();
            final WorldCoordinate worldCoordinate = WorldCoordinate.parseDouble(pReader, false);
            if (pReader.canRead() && pReader.peek() == ' ') {
                pReader.skip();
                final WorldCoordinate worldCoordinate1 =
                        WorldCoordinate.parseDouble(pReader, pCenterCorrect);
                return new WorldCoordinates(worldcoordinate, worldCoordinate, worldCoordinate1);
            } else {
                pReader.setCursor(i);
                throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
            }
        } else {
            pReader.setCursor(i);
            throw Vec3Argument.ERROR_NOT_COMPLETE.createWithContext(pReader);
        }
    }

    public static WorldCoordinates absolute(final double pX, final double pY, final double pZ) {
        return new WorldCoordinates(new WorldCoordinate(false, pX), new WorldCoordinate(false, pY),
                new WorldCoordinate(false, pZ));
    }

    public static WorldCoordinates absolute(final Vec2 pVector) {
        return new WorldCoordinates(new WorldCoordinate(false, (double) pVector.x),
                new WorldCoordinate(false, (double) pVector.y), new WorldCoordinate(true, 0.0D));
    }

    /**
     * A location with a delta of 0 for all values (equivalent to <code>~ ~ ~</code> or <code>~0
     * ~0 ~0</code>)
     */
    public static WorldCoordinates current() {
        return new WorldCoordinates(new WorldCoordinate(true, 0.0D),
                new WorldCoordinate(true, 0.0D), new WorldCoordinate(true, 0.0D));
    }

    @Override
    public @NonNull Vec3 getPosition(@NonNull final Sender sender) {
        final Vec3 vec3 = sender.getPosition();
        return Vec3.from(this.x.get(vec3.x), this.y.get(vec3.y), this.z.get(vec3.z));
    }

    @Override
    public @NonNull Vec2 getRotation(@NonNull final Sender sender) {
        final Vec2 vec2 = sender.getRotation();
        return Vec2.from((float) this.x.get(vec2.x), (float) this.y.get(vec2.y));
    }

    @Override
    public @NonNull Vec2i getChunkPos(@NonNull final Sender sender) {
        final Vec3 vec3 = sender.getPosition();
        final int chunkX = World.blockToChunkCoordinate(Mth.floor(vec3.x));
        final int chunkZ = World.blockToChunkCoordinate(Mth.floor(vec3.z));

        return Vec2i.from(Mth.floor(this.x.get(chunkX)), Mth.floor(this.z.get(chunkZ)));
    }

    @Override
    public boolean isXRelative() {
        return this.x.isRelative();
    }

    @Override
    public boolean isYRelative() {
        return this.y.isRelative();
    }

    @Override
    public boolean isZRelative() {
        return this.z.isRelative();
    }

    @Override
    public int hashCode() {
        int i = this.x.hashCode();
        i = 31 * i + this.y.hashCode();
        return 31 * i + this.z.hashCode();
    }

    @Override
    public boolean equals(final Object pOther) {
        if (this == pOther) {
            return true;
        } else if (!(pOther instanceof WorldCoordinates)) {
            return false;
        } else {
            final WorldCoordinates worldcoordinates = (WorldCoordinates) pOther;
            if (!this.x.equals(worldcoordinates.x)) {
                return false;
            } else {
                return this.y.equals(worldcoordinates.y) && this.z.equals(worldcoordinates.z);
            }
        }
    }
}
