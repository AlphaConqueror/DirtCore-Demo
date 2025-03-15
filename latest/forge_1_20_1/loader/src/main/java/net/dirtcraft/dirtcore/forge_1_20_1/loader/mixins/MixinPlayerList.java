/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_20_1.loader.mixins;

import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerList.class)
public abstract class MixinPlayerList {

    /**
     * No-op join message. We handle it ourselves.
     *
     * @author AlphaConqueror
     */
    @Redirect(method = "placeNewPlayer(Lnet/minecraft/network/Connection;"
            + "Lnet/minecraft/server/level/ServerPlayer;)V", at = @At(value = "INVOKE", target =
            "Lnet/minecraft/server/players/PlayerList;broadcastSystemMessage"
                    + "(Lnet/minecraft/network/chat/Component;Z)V"))
    public void broadcastSystemMessageInjected(final PlayerList instance, final Component pMessage,
            final boolean pBypassHiddenChat) {
        // no-op
    }
}
