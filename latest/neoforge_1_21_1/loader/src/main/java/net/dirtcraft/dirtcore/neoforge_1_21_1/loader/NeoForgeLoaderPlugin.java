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
