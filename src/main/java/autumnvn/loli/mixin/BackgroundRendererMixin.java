package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;

@Mixin(BackgroundRenderer.class)
public class BackgroundRendererMixin {

    // NoFog
    @Inject(method = "applyFog", at = @At("TAIL"))
    private static void applyFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance,
            boolean thickFog, float tickDelta, CallbackInfo ci) {
        if (fogType == BackgroundRenderer.FogType.FOG_TERRAIN) {
            RenderSystem.setShaderFogStart(Short.MAX_VALUE - 1);
            RenderSystem.setShaderFogEnd(Short.MAX_VALUE);
        }
    }
}
