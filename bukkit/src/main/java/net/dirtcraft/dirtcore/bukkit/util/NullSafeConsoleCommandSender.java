/*
 * Copyright (c) 2024 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.bukkit.util;

import java.util.Optional;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * The {@link Server#getConsoleSender()} method returns null during onEnable
 * in older CraftBukkit builds. This prevents LuckPerms from loading correctly.
 */
public class NullSafeConsoleCommandSender implements ConsoleCommandSender {

    private final Server server;

    public NullSafeConsoleCommandSender(final Server server) {
        this.server = server;
    }

    @Override
    public @NonNull Server getServer() {
        return this.server;
    }

    @Override
    public @NonNull String getName() {
        return "CONSOLE";
    }

    @Override
    public void sendMessage(@NonNull final String message) {
        final Optional<ConsoleCommandSender> console = this.get();

        if (console.isPresent()) {
            console.get().sendMessage(message);
        } else {
            this.server.getLogger().info(ChatColor.stripColor(message));
        }
    }

    @Override
    public void sendMessage(final String[] messages) {
        for (final String msg : messages) {
            this.sendMessage(msg);
        }
    }

    @Override
    public boolean isPermissionSet(@NonNull final String s) {
        return this.get().map(c -> c.isPermissionSet(s)).orElse(true);
    }

    @Override
    public boolean isPermissionSet(@NonNull final Permission permission) {
        return this.get().map(c -> c.isPermissionSet(permission)).orElse(true);
    }

    @Override
    public boolean hasPermission(@NonNull final String s) {
        return this.get().map(c -> c.hasPermission(s)).orElse(true);
    }

    @Override
    public boolean hasPermission(@NonNull final Permission permission) {
        return this.get().map(c -> c.hasPermission(permission)).orElse(true);
    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(final boolean b) {throw new UnsupportedOperationException();}

    @Override
    public @NonNull Spigot spigot() {
        return this.get().map(CommandSender::spigot).orElse(new Spigot());
    }

    // just throw UnsupportedOperationException - we never use any of these methods
    @Override
    public boolean isConversing() {throw new UnsupportedOperationException();}

    @Override
    public void acceptConversationInput(
            @NonNull final String s) {throw new UnsupportedOperationException();}

    @Override
    public boolean beginConversation(
            @NonNull final Conversation conversation) {throw new UnsupportedOperationException();}

    @Override
    public void abandonConversation(
            @NonNull final Conversation conversation) {throw new UnsupportedOperationException();}

    @Override
    public void abandonConversation(@NonNull final Conversation conversation,
            @NonNull final ConversationAbandonedEvent conversationAbandonedEvent) {throw new UnsupportedOperationException();}

    @Override
    public void sendRawMessage(@NonNull final String s) {throw new UnsupportedOperationException();}

    @Override
    public @NonNull PermissionAttachment addAttachment(@NonNull final Plugin plugin,
            @NonNull final String s, final boolean b) {throw new UnsupportedOperationException();}

    @Override
    public @NonNull PermissionAttachment addAttachment(
            @NonNull final Plugin plugin) {throw new UnsupportedOperationException();}

    @Override
    public PermissionAttachment addAttachment(@NonNull final Plugin plugin, @NonNull final String s,
            final boolean b, final int i) {throw new UnsupportedOperationException();}

    @Override
    public PermissionAttachment addAttachment(@NonNull final Plugin plugin,
            final int i) {throw new UnsupportedOperationException();}

    @Override
    public void removeAttachment(
            @NonNull final PermissionAttachment permissionAttachment) {throw new UnsupportedOperationException();}

    @Override
    public void recalculatePermissions() {throw new UnsupportedOperationException();}

    @Override
    public @NonNull Set<PermissionAttachmentInfo> getEffectivePermissions() {throw new UnsupportedOperationException();}

    private Optional<ConsoleCommandSender> get() {
        //noinspection ConstantConditions
        return Optional.ofNullable(this.server.getConsoleSender());
    }
}
