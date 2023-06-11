package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;

@Mixin(InGameHud.class)
public class IngameHudMixin {
    @Shadow
    @Final
    private MinecraftClient client;

    @Shadow
    private LivingEntity getRiddenEntity() {
        return null;
    }

    @Shadow
    private int getHeartCount(LivingEntity entity) {
        return 0;
    }

    @Shadow
    private int getHeartRows(int heartCount) {
        return 0;
    }

    @ModifyVariable(method = "renderMountHealth", at = @At(value = "STORE"), ordinal = 2)
    private int onRenderMountHealth(int y) {
        if (client.interactionManager.hasStatusBars())
            y -= 10;
        return y;
    }

    @Redirect(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"))
    private int renderFood(InGameHud inGameHud, LivingEntity entity) {
        return 0;
    }

    @ModifyVariable(method = "renderStatusBars", at = @At(value = "STORE", ordinal = 1), ordinal = 10)
    private int onRenderRiddenEntityHeartBar(int y) {
        LivingEntity entity = getRiddenEntity();
        if (entity != null) {
            int rows = getHeartRows(getHeartCount(entity));
            y -= rows * 10;
        }
        return y;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;"))
    private JumpingMount onRenderJumpMountBar(ClientPlayerEntity player) {
        if (!client.interactionManager.hasExperienceBar() || client.options.jumpKey.isPressed()
                || player.getMountJumpStrength() > 0)
            return player.getJumpingMount();
        return null;
    }
}
