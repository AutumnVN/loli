package autumnvn.ass;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult.Type;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASS implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ass");
	public static MinecraftClient client = MinecraftClient.getInstance();
	public static boolean died = false;
	public static int deathX = 0;
	public static int deathY = 0;
	public static int deathZ = 0;
	public static String deathWorld = "";
	public static boolean mobHealth = false;
	public static boolean triggerBot = false;
	public static KeyBinding zoomKey;
	private static SimpleOption<Double> mouseSens;
	private static double defaultMouseSens;

	@Override
	public void onInitialize() {
		KeyBinding chatCoordsKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.chatCoords", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Y, "AutumnVN's silly stuffs"));
		KeyBinding mobHealthKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.mobHealth", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "AutumnVN's silly stuffs"));
		KeyBinding triggerBotKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.triggerBot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "AutumnVN's silly stuffs"));
		zoomKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "AutumnVN's silly stuffs"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (chatCoordsKey.wasPressed()) {
				int x = (int) client.player.getX();
				int y = (int) client.player.getY();
				int z = (int) client.player.getZ();
				String world = client.world.getRegistryKey().getValue().toString().split(":")[1];
				int health = (int) client.player.getHealth();
				client.player.networkHandler
						.sendChatMessage(String.format("%d / %d / %d in %s | %d ❤", x, y, z, world, health));
			}
			while (mobHealthKey.wasPressed()) {
				mobHealth = !mobHealth;
				client.player.sendMessage(
						Text.literal(mobHealth ? "§aMobHealth is enabled" : "§cMobHealth is disabled"), true);
			}
			while (triggerBotKey.wasPressed()) {
				triggerBot = !triggerBot;
				client.player.sendMessage(
						Text.literal(triggerBot ? "§aTriggerBot is enabled" : "§cTriggerBot is disabled"), true);
			}
		});

		ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
			if (triggerBot && client.crosshairTarget != null && client.crosshairTarget.getType() == Type.ENTITY
					&& client.player.getAttackCooldownProgress(0.0f) >= 1.0f) {
				if (((EntityHitResult) client.crosshairTarget).getEntity() instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.crosshairTarget)
							.getEntity();
					if (livingEntity.isAttackable()
							&& (livingEntity.hurtTime == 0 || livingEntity instanceof WitherEntity)
							&& livingEntity.isAlive()) {
						client.interactionManager.attackEntity(client.player, livingEntity);
						client.player.swingHand(Hand.MAIN_HAND);
					}
				}
			}
		});
	}

	public static double zoomFov(double fov) {
		mouseSens = client.options.getMouseSensitivity();
		if (!zoomKey.isPressed()) {
			if (defaultMouseSens != 0) {
				mouseSens.setValue(defaultMouseSens);
				defaultMouseSens = 0;
			}
			return fov;
		}
		if (defaultMouseSens == 0)
			defaultMouseSens = mouseSens.getValue();
		mouseSens.setValue(defaultMouseSens / 4);
		return fov / 4;
	}
}
