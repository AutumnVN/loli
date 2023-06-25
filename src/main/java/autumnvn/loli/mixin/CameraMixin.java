package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.CameraSubmersionType;
import net.minecraft.entity.Entity;

@Mixin(Camera.class)
public class CameraMixin {

    // 3rdCameraNoClip
    @Inject(method = "clipToSpace(D)D", at = @At("HEAD"), cancellable = true)
    private void onClipToSpace(double desiredCameraDistance, CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(desiredCameraDistance);
    }

    // NoSubmersion
    @Inject(method = "getSubmersionType", at = @At("HEAD"), cancellable = true)
    private void onGetSubmersionType(CallbackInfoReturnable<CameraSubmersionType> cir) {
        cir.setReturnValue(CameraSubmersionType.NONE);
    }

    @Shadow
    private float cameraY;

    @Shadow
    private Entity focusedEntity;

    // InstantSneak
    @Inject(method = "updateEyeHeight", at = @At("HEAD"))
    private void onUpdateEyeHeight(CallbackInfo ci) {
        if (focusedEntity != null) {
            cameraY = focusedEntity.getStandingEyeHeight();
        }
    }
}
