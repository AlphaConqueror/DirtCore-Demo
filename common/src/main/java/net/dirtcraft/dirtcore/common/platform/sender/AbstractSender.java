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

package net.dirtcraft.dirtcore.common.platform.sender;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.dirtcraft.dirtcore.common.model.Sender;
import net.dirtcraft.dirtcore.common.model.minecraft.Entity;
import net.dirtcraft.dirtcore.common.model.minecraft.World;
import net.dirtcraft.dirtcore.common.plugin.DirtCorePlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Simple implementation of {@link Sender} using a {@link SenderFactory}.
 *
 * @param <T> the command sender type
 */
public class AbstractSender<T> implements Sender {

    protected final DirtCorePlugin plugin;
    protected final SenderFactory<?, T> factory;
    protected final T sender;

    protected final UUID uniqueId;
    protected final String name;
    protected final boolean isConsole;

    protected AbstractSender(final DirtCorePlugin plugin, final SenderFactory<?, T> factory,
            final T sender) {
        this.plugin = plugin;
        this.factory = factory;
        this.sender = sender;
        this.uniqueId = factory.getUniqueId(this.sender);
        this.name = factory.getName(this.sender);
        this.isConsole = this.factory.isConsole(this.sender);
    }

    // A small utility method which splits components built using
    // > join(newLine(), components...)
    // back into separate components.
    public static Iterable<Component> splitNewlines(final Component message) {
        if (message instanceof TextComponent && message.style().isEmpty() && !message.children()
                .isEmpty() && ((TextComponent) message).content().isEmpty()) {
            final LinkedList<List<Component>> split = new LinkedList<>();

            split.add(new ArrayList<>());

            for (final Component child : message.children()) {
                if (Component.newline().equals(child)) {
                    split.add(new ArrayList<>());
                } else {
                    final Iterator<Component> splitChildren = splitNewlines(child).iterator();
                    if (splitChildren.hasNext()) {
                        split.getLast()
                                .add(splitChildren.next());
                    }
                    while (splitChildren.hasNext()) {
                        split.add(new ArrayList<>());
                        split.getLast()
                                .add(splitChildren.next());
                    }
                }
            }

            return Iterables.transform(split, input -> {
                switch (input.size()) {
                    case 0:
                        return Component.empty();
                    case 1:
                        return input.get(0);
                    default:
                        return Component.join(JoinConfiguration.separator(Component.empty()),
                                input);
                }
            });
        }

        return Collections.singleton(message);
    }

    @Override
    public @NonNull DirtCorePlugin getPlugin() {
        return this.plugin;
    }

    @Override
    public void sendMessage(final Component message) {
        if (this.isConsole()) {
            for (final Component line : splitNewlines(message)) {
                this.factory.sendMessage(this.sender, line);
            }
        } else {
            this.factory.sendMessage(this.sender, message);
        }
    }

    @Override
    public void sendMessage(final Iterable<Component> messages) {
        this.factory.sendMessage(this.sender, messages);
    }

    @Override
    public boolean isConsole() {
        return this.isConsole;
    }

    @Override
    public boolean isEntity() {
        return this.factory.isEntity(this.sender);
    }

    @Override
    public @NonNull Optional<Entity> getEntity() {
        return this.factory.getEntity(this.sender);
    }

    @Override
    public @NonNull Optional<World> getWorld() {
        return this.factory.getWorld(this.sender);
    }

    @Override
    public @NonNull UUID getUniqueId() {
        return this.uniqueId;
    }

    @Override
    public @NonNull String getName() {
        return this.name;
    }

    @Override
    public boolean hasPermission(final @NonNull String permission) {
        return this.plugin.getPermissionHandler().hasPermission(this.uniqueId, permission);
    }

    @Override
    public boolean isPartOfGroup(@NonNull final String name) {
        return this.plugin.getPermissionHandler().isPartOfGroup(this.uniqueId, name);
    }

    @Override
    public @NonNull Collection<String> getGroups() {
        return this.plugin.getPermissionHandler().getGroups(this.uniqueId);
    }

    @Override
    public @NonNull Collection<TextCoordinates> getRelevantCoordinates() {
        return this.factory.getRelevantCoordinates(this.sender);
    }

    @Override
    public @NonNull Collection<TextCoordinates> getAbsoluteCoordinates() {
        return this.factory.getAbsoluteCoordinates(this.sender);
    }

    @Override
    public @NonNull Collection<String> getSelectedEntities() {
        return this.factory.getSelectedEntities(this.sender);
    }

    @Override
    public int hashCode() {
        return this.uniqueId.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof AbstractSender)) {
            return false;
        }

        final AbstractSender<?> that = (AbstractSender<?>) o;

        return this.getUniqueId().equals(that.getUniqueId());
    }
}
