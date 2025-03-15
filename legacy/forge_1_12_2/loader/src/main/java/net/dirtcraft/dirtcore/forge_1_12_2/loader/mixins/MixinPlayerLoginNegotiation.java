/*
 * Copyright (c) 2025 Marc Beckhaeuser (AlphaConqueror) <marcbeckhaeuser@gmail.com>
 *
 * Created for 'DirtCraft'.
 *
 * ALL RIGHTS RESERVED.
 */

package net.dirtcraft.dirtcore.forge_1_12_2.loader.mixins;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import net.dirtcraft.dirtcore.forge_1_12_2.api.event.player.PlayerNegotiationEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, remap = false)
public abstract class MixinPlayerLoginNegotiation {

    @Final
    @Shadow(aliases = "field_148546_d")
    private static Logger LOGGER;

    /**
     * {@link PlayerList#initializeConnectionToPlayer(NetworkManager,
     * EntityPlayerMP, NetHandlerPlayServer)}
     */
    @Inject(at = @At("HEAD"), method = "initializeConnectionToPlayer", cancellable = true)
    public void initializeConnectionToPlayer(final NetworkManager netManager,
            final EntityPlayerMP player, final NetHandlerPlayServer nethandlerplayserver,
            final CallbackInfo ci) {
        final List<Future<Void>> pendingFutures = new ArrayList<>();
        final PlayerNegotiationEvent event =
                new PlayerNegotiationEvent(netManager, player.getGameProfile(), pendingFutures);

        MinecraftForge.EVENT_BUS.post(event);
        pendingFutures.removeIf(future -> {
            if (!future.isDone()) {
                return false;
            }

            try {
                future.get();
            } catch (final ExecutionException e) {
                LOGGER.error("Error during negotiation", e.getCause());
            } catch (final CancellationException | InterruptedException ignored) {
                // no-op
            }

            return true;
        });

        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    /**
     * No-op join message. We handle it ourselves.
     *
     * @author AlphaConqueror
     */
    @Redirect(method = "initializeConnectionToPlayer", at = @At(value = "INVOKE", target = "Lnet"
            + "/minecraft/server/management/PlayerList;sendMessage"
            + "(Lnet/minecraft/util/text/ITextComponent;)V"))
    public void sendChatMsgInjected(final PlayerList instance, final ITextComponent component) {
        // no-op
    }
}
