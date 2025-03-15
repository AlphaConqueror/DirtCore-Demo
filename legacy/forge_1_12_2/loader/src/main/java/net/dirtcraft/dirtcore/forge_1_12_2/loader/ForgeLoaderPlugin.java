/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Optional;
import java.util.function.Supplier;
import net.dirtcraft.dirtcore.forge_1_12_2.DirtCoreForgeBootstrap;
import net.dirtcraft.dirtcore.forge_1_12_2.ForgeEventDispatcher;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MetadataCollection;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

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

    public static Optional<ForgeEventDispatcher> getEventDispatcher() {
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
