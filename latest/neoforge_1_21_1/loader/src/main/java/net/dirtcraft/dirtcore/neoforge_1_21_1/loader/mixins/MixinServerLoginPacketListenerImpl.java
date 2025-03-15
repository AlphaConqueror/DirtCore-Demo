/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.neoforge_1_21_1.loader.mixins;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import net.dirtcraft.dirtcore.neoforge_1_21_1.api.event.player.PlayerNegotiationEvent;
import net.minecraft.network.Connection;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerLoginPacketListenerImpl.class)
public abstract class MixinServerLoginPacketListenerImpl {

    @Shadow
    @Final
    Connection connection;
    @Shadow
    private GameProfile authenticatedProfile;

    @Inject(method = "tick", at = @At(value = "INVOKE", target =
            "verifyLoginAndFinishConnectionSetup"
                    + "(Lcom/mojang/authlib/GameProfile;)V", shift = At.Shift.BEFORE),
            cancellable = true)
    public void tick(final CallbackInfo ci) {
        final PlayerNegotiationEvent event =
                new PlayerNegotiationEvent(this.connection, this.authenticatedProfile,
                        Collections.emptyList());
        NeoForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ci.cancel();
        }
    }
}
