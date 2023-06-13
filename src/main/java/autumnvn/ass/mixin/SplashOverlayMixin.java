package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
    @Shadow
    private long reloadCompleteTime;

    @Shadow
    @Final
    private MinecraftClient client;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.reloadCompleteTime > 1) {
            this.client.setOverlay(null);
        }
    }
}
