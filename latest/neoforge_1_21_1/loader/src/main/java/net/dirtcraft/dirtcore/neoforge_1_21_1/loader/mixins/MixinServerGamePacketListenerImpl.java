/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.loader.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class MixinServerGamePacketListenerImpl {

    /**
     * No-op leave message. We handle it ourselves.
     *
     * @author AlphaConqueror
     */
    @Redirect(method = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;"
            + "removePlayerFromWorld()V", at = @At(value = "INVOKE", target = "Lnet/minecraft"
            + "/server/players/PlayerList;broadcastSystemMessage"
            + "(Lnet/minecraft/network/chat/Component;Z)V"))
    public void broadcastSystemMessageInjected(final PlayerList instance, final Component pMessage,
            final boolean pBypassHiddenChat) {
        // no-op
    }
}
