package autumnvn.ass.mixin;

import java.util.ArrayList;
import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Ordering;

import autumnvn.ass.ASS;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

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

    // MountHud
    @ModifyVariable(method = "renderMountHealth", at = @At("STORE"), ordinal = 2)
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
    private int renderRiddenEntityHeartBar(int y) {
        if (getRiddenEntity() != null)
            y -= getHeartRows(getHeartCount(getRiddenEntity())) * 10;
        return y;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;getJumpingMount()Lnet/minecraft/entity/JumpingMount;"))
    private JumpingMount renderJumpMountBar(ClientPlayerEntity player) {
        if (!client.interactionManager.hasExperienceBar() || client.options.jumpKey.isPressed()
                || player.getMountJumpStrength() > 0)
            return player.getJumpingMount();
        return null;
    }

    // StatusEffectTimer
    @Inject(method = "renderStatusEffectOverlay", at = @At("TAIL"))
    private void onRenderStatusEffectOverlay(DrawContext drawContext, CallbackInfo ci) {
        Collection<StatusEffectInstance> collection = this.client.player.getStatusEffects();
        if (!collection.isEmpty()) {
            int beneficialCount = 0;
            int nonBeneficialCount = 0;

            for (StatusEffectInstance statusEffectInstance : Ordering.natural().reverse().sortedCopy(collection)) {
                StatusEffect statusEffect = statusEffectInstance.getEffectType();

                if (statusEffectInstance.shouldShowIcon()) {
                    int x = this.client.getWindow().getScaledWidth();
                    int y = 1;

                    if (this.client.isDemo())
                        y += 15;

                    if (statusEffect.isBeneficial())
                        x -= 25 * ++beneficialCount;
                    else {
                        x -= 25 * ++nonBeneficialCount;
                        y += 26;
                    }

                    String duration = getDurationAsString(statusEffectInstance);

                    if (statusEffectInstance.isInfinite())
                        duration = "∞";

                    int durationLength = client.textRenderer.getWidth(duration);
                    drawContext.drawTextWithShadow(client.textRenderer, duration, x + 13 - (durationLength / 2), y + 14,
                            0xffffff);
                    int amplifier = statusEffectInstance.getAmplifier();

                    if (amplifier > 0) {
                        String amplifierString = (amplifier < 6) ? I18n.translate("potion.potency." + amplifier) : "**";
                        int amplifierLength = client.textRenderer.getWidth(amplifierString);
                        drawContext.drawTextWithShadow(client.textRenderer, amplifierString, x + 22 - amplifierLength,
                                y + 3, 0xffffff);
                    }
                }
            }
        }
    }

    @Shadow
    private int scaledWidth;

    @Shadow
    private int scaledHeight;

    @Shadow
    private void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack,
            int seed) {
    }

    // ArmorHud
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderArmorHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        int x = 68;
        int y = this.scaledHeight - 55;

        if (client.player.getAir() < client.player.getMaxAir())
            y -= 10;

        if (client.player.isCreative()) {
            y += 16;
            if (client.player.hasVehicle() && getRiddenEntity() != null && getRiddenEntity().isAlive())
                y -= 6;
        }

        if (client.player.hasVehicle() && getRiddenEntity() != null && getRiddenEntity().isAlive()) {
            if (getRiddenEntity().getMaxHealth() > 21)
                y -= 20;
            else
                y -= 10;
        }

        for (int i = 0; i < 4; i++) {
            renderHotbarItem(context, this.scaledWidth / 2 + x, y, tickDelta, client.player,
                    client.player.getInventory().getArmorStack(i), 1);
            x -= 15;
        }
    }

    private String getDurationAsString(StatusEffectInstance statusEffectInstance) {
        int ticks = MathHelper.floor((float) statusEffectInstance.getDuration());
        int seconds = ticks / 20;

        if (ticks > 32147)
            return "**";

        if (seconds > 60 & seconds < 600)
            return seconds / 60 + ":" + seconds % 60;

        if (seconds > 600)
            return seconds / 60 + "m";

        return String.valueOf(seconds);
    }

    // InfoHud
    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderInfoHud(DrawContext context, float tickDelta, CallbackInfo ci) {
        if (client.options.debugEnabled)
            return;

        ArrayList<String> lines = new ArrayList<>();

        Direction direction = client.player.getHorizontalFacing();

        String offset = "";

        if (direction.getOffsetX() > 0)
            offset += "+X";
        else if (direction.getOffsetX() < 0)
            offset += "-X";

        if (direction.getOffsetZ() > 0)
            offset += "+Z";
        else if (direction.getOffsetZ() < 0)
            offset += "-Z";

        lines.add(String.format("%d fps", client.getCurrentFps()));
        lines.add(String.format("%d, %d, %d", client.player.getBlockPos().getX(), client.player.getBlockPos().getY(),
                client.player.getBlockPos().getZ()));
        lines.add(String.format("%s %s", cap(direction.asString()), offset));
        lines.add(String.format("%.1f tps", ASS.tps));
        if (!client.isInSingleplayer()
                && client.getNetworkHandler().getPlayerListEntry(client.player.getUuid()) != null)
            lines.add(String.format("%dms",
                    client.getNetworkHandler().getPlayerListEntry(client.player.getUuid()).getLatency()));

        for (String line : lines) {
            context.drawText(client.textRenderer, line, 2,
                    2 + (lines.indexOf(line) * (client.textRenderer.fontHeight + 2)), 0xffffff, false);
        }
    }

    private static String cap(String str) {
        if (str == null)
            return null;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
