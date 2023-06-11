package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    MinecraftClient client = MinecraftClient.getInstance();

    @ModifyVariable(method = "render", at = @At(value = "STORE"), ordinal = 7)
    private int playerList(int width) {
        return width + client.textRenderer.getWidth("9999ms");
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/PlayerListHud;renderLatencyIcon(Lnet/minecraft/client/gui/DrawContext;IIILnet/minecraft/client/network/PlayerListEntry;)V"))
    private void renderLatencyIcon(PlayerListHud playerListHud, DrawContext drawContext, int width, int x, int y,
            PlayerListEntry playerListEntry) {
        int ping = playerListEntry.getLatency();
        int offset = client.textRenderer.getWidth(ping + "ms");
        drawContext.drawTextWithShadow(client.textRenderer, ping + "ms", x + width - offset, y, getPingColor(ping));
    }

    private int getPingColor(int ping) {
        if (ping > 300)
            return 0xff5252;
        if (ping > 150)
            return 0xffba52;
        return 0x66ff88;
    }
}
