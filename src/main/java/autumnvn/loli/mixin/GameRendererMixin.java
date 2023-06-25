package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import autumnvn.loli.Loli;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    // Zoom
    @Inject(method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", at = @At(value = "RETURN", ordinal = 1), cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(Loli.zoomFov(cir.getReturnValueD()));
    }

    // DiscordRPC
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(float f, long l, boolean bl, CallbackInfo ci) {
        Loli.updateScreen();
    }
}
