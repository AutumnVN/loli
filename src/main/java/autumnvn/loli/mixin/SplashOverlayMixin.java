package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screen.SplashOverlay;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {

    @Shadow
    private long reloadCompleteTime;

    // NoFade
    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(CallbackInfo ci) {
        if (reloadCompleteTime > 0)
            reloadCompleteTime = 0;
    }
}
