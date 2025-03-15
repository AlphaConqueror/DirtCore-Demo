/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.loader;

import java.util.Optional;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.loader.JarInJarClassLoader;
import net.dirtcraft.dirtcore.common.loader.LoaderBootstrap;
import net.dirtcraft.dirtcore.common.loader.event.LoaderEventDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = "@mod_id@", dist = Dist.DEDICATED_SERVER)
public class NeoForgeLoaderPlugin implements Supplier<ModContainer> {

    private static final Logger LOGGER = LogManager.getLogger("@mod_id@");
    private static final String JAR_NAME = "DirtCore-NeoForge-1.21.1.jarinjar";
    private static final String BOOTSTRAP_CLASS =
            "net.dirtcraft.dirtcore.neoforge_1_21_1.DirtCoreNeoForgeBootstrap";
    private static LoaderBootstrap<BlockState, ItemStack, Level> INSTANCE;
    private final ModContainer container;

    private JarInJarClassLoader loader;

    public NeoForgeLoaderPlugin(final IEventBus modEventBus, final ModContainer modContainer) {
        this.container = modContainer;

        if (FMLEnvironment.dist.isClient()) {
            LOGGER.info("Skipping DirtCore init (not supported on the client!)");
            return;
        }

        this.loader = new JarInJarClassLoader(this.getClass().getClassLoader(), JAR_NAME);
        modEventBus.addListener(this::onCommonSetup);
    }

    public static Optional<LoaderEventDispatcher<BlockState, ItemStack, Level>> getEventDispatcher() {
        return Optional.ofNullable(INSTANCE.getEventDispatcher());
    }

    @Override
    public ModContainer get() {
        return this.container;
    }

    public void onCommonSetup(final FMLCommonSetupEvent event) {
        //noinspection unchecked
        INSTANCE = (LoaderBootstrap<BlockState, ItemStack, Level>) this.loader.instantiatePlugin(
                BOOTSTRAP_CLASS, Supplier.class, this);
        INSTANCE.onLoad();
    }
}
