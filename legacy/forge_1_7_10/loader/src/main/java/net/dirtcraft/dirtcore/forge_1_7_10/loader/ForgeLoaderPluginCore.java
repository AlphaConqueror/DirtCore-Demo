/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import java.util.Map;
import org.spongepowered.asm.mixin.Mixins;

public class ForgeLoaderPluginCore implements IFMLLoadingPlugin {

    public ForgeLoaderPluginCore() {
        // TODO: Check if this is needed.
        // TODO: Check for unused entries in refmap.
        // TODO: Maybe replace SRG with accessors.
        try {
            Mixins.addConfiguration("dirtcore.mixins.json");
        } catch (final Exception ignored) {}
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return ForgeLoaderPlugin.class.getCanonicalName();
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(final Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
