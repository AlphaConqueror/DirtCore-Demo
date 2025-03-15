/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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
