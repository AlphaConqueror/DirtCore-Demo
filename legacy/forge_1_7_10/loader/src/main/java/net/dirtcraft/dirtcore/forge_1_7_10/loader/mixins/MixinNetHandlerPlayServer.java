/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_7_10.loader.mixins;

import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IChatComponent;
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
            + "/management/ServerConfigurationManager;sendChatMsg"
            + "(Lnet/minecraft/util/IChatComponent;)V"))
    public void sendChatMsgInjected(final ServerConfigurationManager instance,
            final IChatComponent component) {
        // no-op
    }
}
