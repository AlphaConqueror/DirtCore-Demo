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

package net.dirtcraft.dirtcore.common.util.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JArray implements JElement {

    private final JsonArray array = new JsonArray();

    @Override
    public JsonArray toJson() {
        return this.array;
    }

    public JArray add(final JsonElement value) {
        if (value == null) {
            return this.add(JsonNull.INSTANCE);
        }
        this.array.add(value);
        return this;
    }

    public JArray add(final String value) {
        if (value == null) {
            return this.add(JsonNull.INSTANCE);
        }
        this.array.add(new JsonPrimitive(value));
        return this;
    }

    public JArray addAll(final Iterable<String> iterable) {
        for (final String s : iterable) {
            this.add(s);
        }
        return this;
    }

    public JArray add(final JElement value) {
        if (value == null) {
            return this.add(JsonNull.INSTANCE);
        }
        return this.add(value.toJson());
    }

    public JArray add(final Supplier<? extends JElement> value) {
        return this.add(value.get().toJson());
    }

    public JArray consume(final Consumer<? super JArray> consumer) {
        consumer.accept(this);
        return this;
    }
}