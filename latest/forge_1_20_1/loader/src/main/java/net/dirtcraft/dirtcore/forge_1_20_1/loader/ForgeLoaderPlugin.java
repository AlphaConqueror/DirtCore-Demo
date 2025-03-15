/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.loader;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.common.loader.JarInJarClassLoader;
import net.dirtcraft.dirtcore.common.loader.LoaderBootstrap;
import net.dirtcraft.dirtcore.common.loader.event.LoaderEventDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(value = "@mod_id@")
public class ForgeLoaderPlugin implements Supplier<ModContainer> {

    private static final Logger LOGGER = LogManager.getLogger("@mod_id@");
    private static final String JAR_NAME = "DirtCore-Forge-1.20.1.jarinjar";
    private static final String BOOTSTRAP_CLASS =
            "net.dirtcraft.dirtcore.forge_1_20_1" + ".DirtCoreForgeBootstrap";
    private static LoaderBootstrap<BlockState, ItemStack, Level> INSTANCE;
    private final ModContainer container;

    private JarInJarClassLoader loader;

    public ForgeLoaderPlugin() {
        this.container = ModList.get().getModContainerByObject(this).orElse(null);

        markAsNotRequiredClientSide();

        if (FMLEnvironment.dist.isClient()) {
            LOGGER.info("Skipping DirtCore init (not supported on the client!)");
            return;
        }

        this.loader = new JarInJarClassLoader(this.getClass().getClassLoader(), JAR_NAME);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onCommonSetup);
    }

    private static void markAsNotRequiredClientSide() {
        try {
            // workaround as we don't compile against java 17
            ModLoadingContext.class.getDeclaredMethod("registerExtensionPoint", Class.class,
                            Supplier.class)
                    .invoke(ModLoadingContext.get(), IExtensionPoint.DisplayTest.class,
                            (Supplier<?>) () -> new IExtensionPoint.DisplayTest(
                                    () -> IExtensionPoint.DisplayTest.IGNORESERVERONLY,
                                    (a, b) -> true));
        } catch (final IllegalAccessException | InvocationTargetException |
                       NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
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
