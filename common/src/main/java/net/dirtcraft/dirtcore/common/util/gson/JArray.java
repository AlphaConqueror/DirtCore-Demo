/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
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