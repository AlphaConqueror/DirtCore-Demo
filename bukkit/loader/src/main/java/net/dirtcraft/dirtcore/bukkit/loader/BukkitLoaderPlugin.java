/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.loader;

import net.dirtcraft.dirtcore.common.loader.JarInJarClassLoader;
import net.dirtcraft.dirtcore.common.loader.LoaderBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class BukkitLoaderPlugin extends JavaPlugin {

    private static final String JAR_NAME = "DirtCore-Bukkit.jarinjar";
    private static final String BOOTSTRAP_CLASS =
            "net.dirtcraft.dirtcore.bukkit" + ".DirtCoreBukkitBootstrap";
    private final LoaderBootstrap plugin;

    public BukkitLoaderPlugin() {
        //noinspection resource
        final JarInJarClassLoader loader =
                new JarInJarClassLoader(this.getClass().getClassLoader(), JAR_NAME);
        this.plugin = loader.instantiatePlugin(BOOTSTRAP_CLASS, JavaPlugin.class, this);
    }

    @Override
    public void onLoad() {
        this.plugin.onLoad();
    }

    @Override
    public void onEnable() {
        this.plugin.onEnable();
    }

    @Override
    public void onDisable() {
        this.plugin.onDisable();
    }
}
