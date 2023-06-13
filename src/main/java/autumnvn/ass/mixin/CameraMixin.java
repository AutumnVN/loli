package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.Camera;

@Mixin(Camera.class)
public class CameraMixin {
    @Inject(at = @At("HEAD"), method = "clipToSpace(D)D", cancellable = true)
    private void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(desiredCameraDistance);
    }
}
