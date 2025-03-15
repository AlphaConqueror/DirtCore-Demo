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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JObject implements JElement {

    private final JsonObject object = new JsonObject();

    @Override
    public JsonObject toJson() {
        return this.object;
    }

    public JObject add(final String key, final JsonElement value) {
        this.object.add(key, value);
        return this;
    }

    public JObject add(final String key, final String value) {
        if (value == null) {
            return this.add(key, JsonNull.INSTANCE);
        }
        return this.add(key, new JsonPrimitive(value));
    }

    public JObject add(final String key, final Number value) {
        if (value == null) {
            return this.add(key, JsonNull.INSTANCE);
        }
        return this.add(key, new JsonPrimitive(value));
    }

    public JObject add(final String key, final Boolean value) {
        if (value == null) {
            return this.add(key, JsonNull.INSTANCE);
        }
        return this.add(key, new JsonPrimitive(value));
    }

    public JObject add(final String key, final JElement value) {
        if (value == null) {
            return this.add(key, JsonNull.INSTANCE);
        }
        return this.add(key, value.toJson());
    }

    public JObject add(final String key, final Supplier<? extends JElement> value) {
        return this.add(key, value.get().toJson());
    }

    public JObject consume(final Consumer<? super JObject> consumer) {
        consumer.accept(this);
        return this;
    }
}
