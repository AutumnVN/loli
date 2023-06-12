package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autumnvn.ass.ASS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;

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

}
