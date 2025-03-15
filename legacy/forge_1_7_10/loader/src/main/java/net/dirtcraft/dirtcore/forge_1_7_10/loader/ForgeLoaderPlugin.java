/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.forge_1_7_10.DirtCoreForgeBootstrap;
import net.dirtcraft.dirtcore.forge_1_7_10.api.event.ForgeLoaderEventDispatcher;
import net.dirtcraft.dirtcore.forge_1_7_10.api.util.ForgeBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mod(modid = "@mod_id@", version = "@version@", acceptableRemoteVersions = "*")
public class ForgeLoaderPlugin extends DummyModContainer implements Supplier<ModContainer> {

    private static DirtCoreForgeBootstrap INSTANCE;
    private final ModContainer container;
    private Path minecraftConfigDir;

    public ForgeLoaderPlugin() {
        super(getModMetadata());
        this.container = Loader.instance().getModList().stream()
                .filter(modContainer -> modContainer.getMod() == this).findFirst().orElse(null);
    }

    public static Optional<ForgeLoaderEventDispatcher<ForgeBlock, ItemStack, World>> getEventDispatcher() {
        return Optional.ofNullable(INSTANCE.getEventDispatcher());
    }

    private static ModMetadata getModMetadata() {
        try {
            final Enumeration<URL> resources =
                    ForgeLoaderPlugin.class.getClassLoader().getResources("mcmod.info");
            final JsonParser jsonParser = new JsonParser();
            final Gson gson = new GsonBuilder().registerTypeAdapter(ArtifactVersion.class,
                    new MetadataCollection.ArtifactVersionAdapter()).create();

            while (resources.hasMoreElements()) {
                try (final InputStream inputStream = resources.nextElement().openStream()) {
                    if (inputStream == null) {
                        continue;
                    }

                    try (final InputStreamReader reader = new InputStreamReader(inputStream)) {
                        final JsonElement root = jsonParser.parse(reader);

                        if (root.isJsonArray()) {
                            final JsonArray jsonArray = root.getAsJsonArray();

                            for (final JsonElement element : jsonArray) {
                                try {
                                    final ModMetadata modMetadata =
                                            gson.fromJson(element, ModMetadata.class);

                                    if (modMetadata.modId.equals("@mod_id@")) {
                                        return modMetadata;
                                    }
                                } catch (final JsonSyntaxException ignored) {
                                    // bad element, skip
                                }
                            }
                        }
                    }
                } catch (final IOException ignored) {
                    // something went wrong while opening the stream, skip
                }
            }
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        throw new UnsupportedOperationException("Could not find 'mcmod.info' for this mod.");
    }

    @Override
    public boolean registerBus(final EventBus bus, final LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public ModContainer get() {
        return this.container;
    }

    @Subscribe
    public void onPreInitialization(final FMLPreInitializationEvent event) {
        this.minecraftConfigDir = event.getModConfigurationDirectory().toPath();
    }

    @Subscribe
    public void onCommonSetup(final FMLInitializationEvent event) {
        INSTANCE = new DirtCoreForgeBootstrap(this, this.minecraftConfigDir);
        INSTANCE.onLoad();
    }

    @Subscribe
    public void onServerAboutToStart(final FMLServerAboutToStartEvent event) {
        INSTANCE.onServerAboutToStart(event);
    }

    @Subscribe
    public void onServerStarted(final FMLServerStartedEvent event) {
        INSTANCE.onServerStarted(event);
    }

    @Subscribe
    public void onServerStopping(final FMLServerStoppingEvent event) {
        INSTANCE.onServerStopping(event);
    }
}
