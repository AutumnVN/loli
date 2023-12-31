package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autumnvn.loli.Loli;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.tutorial.TutorialManager;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Shadow
    private int itemUseCooldown;

    // NoUseDelay
    @Inject(method = "doItemUse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/MinecraftClient;itemUseCooldown:I", ordinal = 0, shift = At.Shift.AFTER))
    private void onItemUseCooldown(CallbackInfo ci) {
        itemUseCooldown = Loli.noUseDelay ? 1 : 4;
    }

    @Shadow
    @Final
    private TutorialManager tutorialManager;

    @Shadow
    public void setScreen(Screen screen) {
    }

    @Shadow
    private ClientPlayerEntity player;

    // PlayerInventoryWhileRiding
    @Redirect(method = "handleInputEvents()V", at = @At(value = "INVOKE", target = "net/minecraft/client/network/ClientPlayerEntity.openRidingInventory ()V"))
    private void playerInventoryAccess(ClientPlayerEntity instance) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.options.sprintKey.isPressed()) {
            tutorialManager.onInventoryOpened();
            setScreen(new InventoryScreen(player));
        } else {
            instance.openRidingInventory();
        }
    }
}
