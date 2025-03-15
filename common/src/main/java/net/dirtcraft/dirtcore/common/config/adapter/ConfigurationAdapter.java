/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.common.config.adapter;

import java.util.List;
import java.util.Map;
import net.dirtcraft.dirtcore.common.logging.Logger;

public interface ConfigurationAdapter {

    Logger getLogger();

    void reload();

    String getString(String path, String def);

    int getInteger(String path, int def);

    long getLong(String path, long def);

    double getDouble(String path, double def);

    boolean getBoolean(String path, boolean def);

    List<String> getStringList(String path, List<String> def);

    List<Integer> getIntList(String path, List<Integer> def);

    List<Long> getLongList(String path, List<Long> def);

    List<Double> getDoubleList(String path, List<Double> def);

    <T> Map<String, Object> getMap(String path, Map<String, T> def);

    Map<String, String> getStringMap(String path, Map<String, String> def);
}
