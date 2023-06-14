package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.screen.slot.SlotActionType;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    // DeathCoords
    @Inject(method = "onDeathMessage", at = @At("HEAD"))
    private void onDeathMessage(DeathMessageS2CPacket packet, CallbackInfo ci) {
        Entity entity = client.world.getEntityById(packet.getEntityId());

        if (entity == client.player) {
            ASS.deathX = (int) client.player.getX();
            ASS.deathY = (int) client.player.getY();
            ASS.deathZ = (int) client.player.getZ();
            ASS.deathWorld = client.player.clientWorld.getRegistryKey().getValue().toString().split(":")[1];
            ASS.died = true;
        }
    }

    // NoInsecureToast
    @Redirect(method = "onServerMetadata(Lnet/minecraft/network/packet/s2c/play/ServerMetadataS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/toast/ToastManager;add(Lnet/minecraft/client/toast/Toast;)V"))
    private void noInsecureChatToast(final ToastManager instance, final Toast toast) {
    }

    // TPS
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        TPS.updateTime(packet.getTime());
    }

    // AutoTotem
    @Inject(method = "onEntityStatus", at = @At("RETURN"))
    private void onOnEntityStatus(EntityStatusS2CPacket packet, CallbackInfo info) {
        if (packet.getStatus() == 35 && packet.getEntity(client.player.getWorld()).equals(client.player)) {
            int totemSlotId = -1;
            if (client.player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING)
                totemSlotId = client.player.getInventory().selectedSlot + 36;
            else if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING)
                totemSlotId = 45;

            if (totemSlotId != -1) {
                int inventorySize = client.player.getInventory().main.size();

                for (int i = 0; i < inventorySize; i++) {
                    if (client.player.getInventory().main.get(i).getItem() == Items.TOTEM_OF_UNDYING
                            && i != client.player.getInventory().selectedSlot) {
                        if (i < 9)
                            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i + 36, 0,
                                    SlotActionType.PICKUP, client.player);
                        else
                            client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, i, 0,
                                    SlotActionType.PICKUP, client.player);

                        client.interactionManager.clickSlot(client.player.playerScreenHandler.syncId, totemSlotId, 0,
                                SlotActionType.PICKUP, client.player);
                        break;
                    }
                }
            }
        }
    }
}
