/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader.mixins;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {

    /**
     * No-op leave message. We handle it ourselves.
     *
     * @author AlphaConqueror
     */
    @Redirect(method = "onDisconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server"
            + "/management/PlayerList;sendMessage(Lnet/minecraft/util/text/ITextComponent;)V"))
    public void sendChatMsgInjected(final PlayerList instance, final ITextComponent component) {
        // no-op
    }
}
