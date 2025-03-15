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

package net.dirtcraft.dirtcore.common.actionlog;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.UUID;
import net.dirtcraft.dirtcore.api.actionlog.Action;
import net.dirtcraft.dirtcore.common.storage.entities.log.LogEntity;
import net.dirtcraft.dirtcore.common.util.gson.JObject;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ActionJsonSerializer {

    private ActionJsonSerializer() {}

    public static JsonObject serialize(@NonNull final Action log) {
        final JObject root = new JObject().add("timestamp",
                        new JsonPrimitive(log.getTimestamp().getEpochSecond()))
                .add("source", log.getSource().toString())
                .add("source_server", log.getSourceServer())
                .add("type", log.getType().getIdentifier())
                .add("authorization", log.getAuthorization().getIdentifier());

        log.getTarget().ifPresent(target -> root.add("target", target.toString()));
        log.getTitle().ifPresent(title -> root.add("title", title));
        log.getDescription().ifPresent(description -> root.add("description", description));
        log.getIncidentId().ifPresent(incidentId -> root.add("incident_id", incidentId));

        return root.toJson();
    }

    public static Action deserialize(@NonNull final JsonElement element) {
        Preconditions.checkArgument(element.isJsonObject());
        final JsonObject data = element.getAsJsonObject();
        final long timestamp = data.get("timestamp").getAsLong();
        final UUID source = UUID.fromString(data.get("source").getAsString());
        final String sourceServer = data.get("source_server").getAsString();
        final Action.Type type = Action.Type.fromString(data.get("type").getAsString());
        final Action.Authorization authorization =
                Action.Authorization.fromString(data.get("authorization").getAsString());
        final LogEntity.Builder builder =
                LogEntity.builder(timestamp, source, sourceServer, type, authorization);

        if (data.has("target")) {
            final UUID target = UUID.fromString(data.get("target").getAsString());
            builder.target(target);
        }

        if (data.has("title")) {
            builder.title(data.get("title").getAsString());
        }

        if (data.has("description")) {
            builder.description(data.get("description").getAsString());
        }

        if (data.has("incident_id")) {
            builder.incidentId(data.get("incident_id").getAsString());
        }

        return builder.build();
    }
}
