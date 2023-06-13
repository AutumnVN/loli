package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autumnvn.ass.ASS;
import autumnvn.ass.command.TPS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @Inject(method = "onDeathMessage", at = @At("HEAD"))
    private void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        Entity entity = client.world.getEntityById(packet.getEntityId());

        if (entity == client.player) {
            ASS.deathX = (int) client.player.getX();
            ASS.deathY = (int) client.player.getY();
            ASS.deathZ = (int) client.player.getZ();
            ASS.deathWorld = client.player.clientWorld.getRegistryKey().getValue().toString().split(":")[1];
            ASS.died = true;
        }
    }

    @Redirect(method = "onServerMetadata(Lnet/minecraft/network/packet/s2c/play/ServerMetadataS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;add(Lnet/minecraft/client/toast/Toast;)V"))
    private void noInsecureChatToast(final ToastManager instance, final Toast toast) {
    }

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void axolotlclient$onWorldUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TPS.updateTime(packet.getTime());
    }

}
