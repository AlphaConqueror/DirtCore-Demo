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

package net.dirtcraft.dirtcore.common.config.adapter;

import com.google.common.base.Splitter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.dirtcraft.dirtcore.common.logging.Logger;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.Types;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public abstract class ConfigurateConfigAdapter implements ConfigurationAdapter {

    private final Logger logger;
    private final Path path;
    private ConfigurationNode root;

    public ConfigurateConfigAdapter(final Logger logger, final Path path) {
        this.logger = logger;
        this.path = path;
        this.reload();
    }

    protected abstract ConfigurationLoader<? extends ConfigurationNode> createLoader(Path path);

    @Override
    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public void reload() {
        final ConfigurationLoader<? extends ConfigurationNode> loader =
                this.createLoader(this.path);

        try {
            this.root = loader.load();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getString(final String path, final String def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getString(def);
    }

    @Override
    public int getInteger(final String path, final int def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getInt(def);
    }

    @Override
    public long getLong(final String path, final long def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getLong(def);
    }

    @Override
    public double getDouble(final String path, final double def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getDouble(def);
    }

    @Override
    public boolean getBoolean(final String path, final boolean def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getBoolean(def);
    }

    @Override
    public List<String> getStringList(final String path, final List<String> def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual() || !node.isList()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getList(Object::toString, def);
    }

    @Override
    public List<Integer> getIntList(final String path, final List<Integer> def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual() || !node.isList()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getList(Types::asInt, def);
    }

    @Override
    public List<Long> getLongList(final String path, final List<Long> def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual() || !node.isList()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getList(Types::asLong, def);
    }

    @Override
    public List<Double> getDoubleList(final String path, final List<Double> def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual() || !node.isList()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        return node.getList(Types::asDouble, def);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, Object> getMap(final String path, final Map<String, T> def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual() || !node.isMap()) {
            this.logKeyNotFound(path, def);
            return (Map<String, Object>) def;
        }

        return (Map<String, Object>) node.getValue(Collections.emptyMap());
    }

    @Override
    public Map<String, String> getStringMap(final String path, final Map<String, String> def) {
        final ConfigurationNode node = this.resolvePath(path);

        if (node.isVirtual() || !node.isMap()) {
            this.logKeyNotFound(path, def);
            return def;
        }

        final Map<String, Object> map = this.getMap(path, def);
        return map.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().toString()));
    }

    private ConfigurationNode resolvePath(final String path) {
        if (this.root == null) {
            throw new UnsupportedOperationException("Config is not loaded.");
        }

        return this.root.getNode(Splitter.on('.').splitToList(path).toArray());
    }

    private void logKeyNotFound(final String path, final Object def) {
        this.logger.warn("Could not find key '{}' in the config. The value of '{}' will be "
                + "used instead.", path, def);
    }
}
