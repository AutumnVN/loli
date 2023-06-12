package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autumnvn.ass.ASS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "setShowsDeathScreen", at = @At("HEAD"))
    private void onSetShowsDeathScreen(CallbackInfo ci) {
        if (ASS.died) {
            ASS.died = false;
            MinecraftClient client = MinecraftClient.getInstance();
            client.player.sendMessage(
                    Text.literal("§6You died at §f" + ASS.deathX + " / " + ASS.deathY + " / " + ASS.deathZ + " §6in §f"
                            + ASS.deathWorld),
                    false);
        }
    }
}
