package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import autumnvn.ass.ASS;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(at = @At(value = "RETURN", ordinal = 1), method = "getFov(Lnet/minecraft/client/render/Camera;FZ)D", cancellable = true)
    private void onGetFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(ASS.zoomFov(cir.getReturnValueD()));
    }
}