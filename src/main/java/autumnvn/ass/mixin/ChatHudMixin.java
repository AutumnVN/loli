package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.MessageIndicator.Icon;

@Mixin(ChatHud.class)
public class ChatHudMixin {

    // 16kChatHistory
    @ModifyConstant(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V", constant = @Constant(intValue = 100))
    private int maxChatHistory(int length) {
        return 16384;
    }

    // NoChatIndicator
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    public void onFill(DrawContext drawContext, int x1, int y1, int x2, int y2, int color) {
        if (x1 == -4 && x2 == -2)
            return;

        drawContext.fill(x1, y1, x2, y2, color);
    }

    @Inject(method = "drawIndicatorIcon", at = @At("HEAD"), cancellable = true)
    public void onDrawIndicatorIcon(DrawContext matrices, int x, int y, Icon icon, CallbackInfo ci) {
        ci.cancel();
    }

    // KeepChatHistory
    @Inject(method = "clear", at = @At("HEAD"), cancellable = true)
    private void onClear(boolean clearHistory, CallbackInfo ci) {
        ci.cancel();
    }
}
