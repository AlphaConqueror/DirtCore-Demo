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
