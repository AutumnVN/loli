package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autumnvn.loli.Loli;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    @Shadow
    @Final
    private MinecraftClient client;

    // DeathCoords
    @Inject(method = "setShowsDeathScreen", at = @At("HEAD"))
    private void onSetShowsDeathScreen(CallbackInfo ci) {
        if (Loli.died) {
            Loli.died = false;
            client.player.sendMessage(Text.literal(
                    "§6You died at §f" + Loli.deathX + " / " + Loli.deathY + " / " + Loli.deathZ + " §6in §f"
                            + Loli.deathWorld),
                    false);
        }
    }
}
