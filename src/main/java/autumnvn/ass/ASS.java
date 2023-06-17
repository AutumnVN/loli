package autumnvn.ass;

import java.util.stream.Collectors;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import autumnvn.ass.command.TPS;
import club.bottomservices.discordrpc.lib.DiscordRPCClient;
import club.bottomservices.discordrpc.lib.EventListener;
import club.bottomservices.discordrpc.lib.RichPresence;
import club.bottomservices.discordrpc.lib.RichPresence.Builder;
import club.bottomservices.discordrpc.lib.User;
import club.bottomservices.discordrpc.lib.exceptions.NoDiscordException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.Block;
import net.minecraft.block.CropBlock;
import net.minecraft.block.NetherWartBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.ProgressScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SchoolingFishEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ASS implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ass");
	public static MinecraftClient client = MinecraftClient.getInstance();

	public static boolean died = false;
	public static int deathX = 0;
	public static int deathY = 0;
	public static int deathZ = 0;
	public static String deathWorld = "";

	public static boolean triggerBot = false;
	public static boolean noUseDelay = false;

	public static KeyBinding zoomKey;
	private static SimpleOption<Double> mouseSens;
	private static double defaultMouseSens;

	private byte rpcTickTimer = 0;
	private static String largeImage = "chino";
	private static String details = null;
	private static String state = null;
	private static String gameState = "mainmenu";

	@Override
	public void onInitialize() {
		KeyBinding chatCoordsKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.chatCoords", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "AutumnVN's silly stuffs"));
		KeyBinding chatItemKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.chatItem", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_I, "AutumnVN's silly stuffs"));
		KeyBinding triggerBotKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.triggerBot", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "AutumnVN's silly stuffs"));
		KeyBinding noUseDelayKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.noUseDelay", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_U, "AutumnVN's silly stuffs"));
		zoomKey = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.zoom", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "AutumnVN's silly stuffs"));

		Builder builder = new RichPresence.Builder().setTimestamps(System.currentTimeMillis() / 1000, null);
		DiscordRPCClient discordClient = new DiscordRPCClient(new EventListener() {
			@Override
			public void onReady(DiscordRPCClient client, User user) {
				LOGGER.info("Discord RPC ready");
				client.sendPresence(builder.build());
			}
		}, "915859850964115527");

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (discordClient.isConnected)
				discordClient.disconnect();
		}, "Discord RPC shutdown hook"));

		try {
			discordClient.connect();
		} catch (NoDiscordException e) {
			LOGGER.error("Failed to connect to Discord Gateway", e);
		}

		new Thread(() -> {
			while (true) {
				if (discordClient.isConnected) {
					discordClient.sendPresence(builder.build());
				} else {
					try {
						discordClient.connect();
					} catch (NoDiscordException e) {
					}
				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}, "Discord RPC update thread").start();

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (chatCoordsKey.wasPressed()) {
				int x = (int) client.player.getX();
				int y = (int) client.player.getY();
				int z = (int) client.player.getZ();
				String world = client.world.getRegistryKey().getValue().toString().split(":")[1];
				int health = (int) client.player.getHealth();
				client.player.networkHandler.sendChatMessage(
						String.format("%d / %d / %d in %s | %d ❤ | %.1f TPS", x, y, z, world, health, TPS.tps));
			}

			while (chatItemKey.wasPressed()) {
				String name = client.player.getMainHandStack().getName().getString();
				int count = client.player.getMainHandStack().getCount();
				String lore = client.player.getMainHandStack().getTooltip(client.player, TooltipContext.Default.BASIC)
						.stream().skip(1).map(Text::getString).collect(Collectors.joining(", "));
				client.player.networkHandler.sendChatMessage(
						String.format("[%s]x%d%s%s", name, count, lore.isEmpty() ? "" : " | ", lore));
			}

			while (triggerBotKey.wasPressed()) {
				triggerBot = !triggerBot;
				client.player.sendMessage(
						Text.literal(triggerBot ? "§aTriggerBot is enabled" : "§cTriggerBot is disabled"), true);
			}

			while (noUseDelayKey.wasPressed()) {
				noUseDelay = !noUseDelay;
				client.player.sendMessage(
						Text.literal(noUseDelay ? "§aNoUseDelay is enabled" : "§cNoUseDelay is disabled"), true);
			}

			if (++rpcTickTimer % 100 == 0) {
				rpcTickTimer = 0;

				if (client.world == null || client.player == null) {
					largeImage = "chino";
					state = null;

					switch (gameState) {
						case "loading":
							details = "Loading World...";
							break;

						case "connecting":
							details = "Connecting to Server...";
							break;

						case "disconnected":
							details = "Disconnected";
							break;

						default:
							details = "Main Menu";
							break;
					}

				} else {
					largeImage = client.world.getRegistryKey().getValue().toString().split(":")[1];
					details = String.format("%.0f💖 %d🍗 %d👕 | %.1f TPS", client.player.getHealth(),
							client.player.getHungerManager().getFoodLevel(), client.player.getArmor(), TPS.tps);
					state = getState();
				}

				builder.setText(details, state);
				builder.setAssets(largeImage,
						String.format("%s is playing Minecraft %s",
								client.getSession().getUsername(), client.getGameVersion()),
						null, null);
			}

			if (client.player != null) {
				if (client.player.hasStatusEffect(StatusEffects.BLINDNESS))
					client.player.removeStatusEffect(StatusEffects.BLINDNESS);
				if (client.player.hasStatusEffect(StatusEffects.DARKNESS))
					client.player.removeStatusEffect(StatusEffects.DARKNESS);
			}
		});

		ClientTickEvents.START_WORLD_TICK.register(clientWorld -> {
			if (triggerBot && client.crosshairTarget != null && client.crosshairTarget.getType() == Type.ENTITY
					&& client.player.getAttackCooldownProgress(0.0f) >= 1.0f) {
				if (((EntityHitResult) client.crosshairTarget).getEntity() instanceof LivingEntity) {
					LivingEntity livingEntity = (LivingEntity) ((EntityHitResult) client.crosshairTarget).getEntity();

					if (livingEntity.isAttackable()
							&& (livingEntity.hurtTime == 0 || livingEntity instanceof WitherEntity)
							&& livingEntity.isAlive()) {
						if (!(livingEntity instanceof PassiveEntity && !(livingEntity instanceof ChickenEntity
								|| livingEntity instanceof CowEntity || livingEntity instanceof PigEntity
								|| livingEntity instanceof PolarBearEntity || livingEntity instanceof PufferfishEntity
								|| livingEntity instanceof RabbitEntity || livingEntity instanceof SchoolingFishEntity
								|| livingEntity instanceof SheepEntity || livingEntity instanceof SquidEntity
								|| livingEntity instanceof TraderLlamaEntity
								|| livingEntity instanceof WanderingTraderEntity))) {
							client.interactionManager.attackEntity(client.player, livingEntity);
							client.player.swingHand(Hand.MAIN_HAND);
						}
					}
				}
			}

			if (client.options.useKey.isPressed() && !client.player.isSneaking() && client.crosshairTarget != null
					&& client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
				if (client.crosshairTarget != null && client.crosshairTarget.getType() == HitResult.Type.BLOCK) {
					final Direction side = ((BlockHitResult) client.crosshairTarget).getSide();
					final BlockPos pos = ((BlockHitResult) client.crosshairTarget).getBlockPos();
					final Block block = client.world.getBlockState(pos).getBlock();

					if ((block instanceof CropBlock cropBlock && cropBlock.isMature(client.world.getBlockState(pos))
							|| (block instanceof NetherWartBlock
									&& client.world.getBlockState(pos).get(NetherWartBlock.AGE) == 3))) {
						client.interactionManager.attackBlock(pos, side);
						client.player.swingHand(Hand.MAIN_HAND);
					}
				}
			}
		});

		ClientCommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
			TPS.register(dispatcher);
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

	public static void updateScreen() {
		if (client.currentScreen instanceof LevelLoadingScreen) {
			gameState = "loading";
		} else if (client.currentScreen instanceof ProgressScreen
				|| client.currentScreen instanceof ConnectScreen) {
			gameState = "connecting";
		} else if (client.currentScreen instanceof DisconnectedScreen) {
			gameState = "disconnected";
		} else {
			gameState = "mainmenu";
		}
	}

	private static String getState() {
		if (client.player.isDead())
			return "Died 💀";

		if (client.player.isSleeping())
			return "Sleeping 💤";

		if (client.player.isInsideWall())
			return "Suffocating 😵";

		if (client.player.isOnFire())
			return "Burning 🔥";

		if (client.player.isFrozen())
			return "Freezing ❄️";

		if (client.player.hasVehicle())
			return "Riding a " + client.player.getVehicle().getName().getString();

		if (client.player.isFallFlying())
			return "Flying 🦅";

		if (client.player.isSwimming())
			return "Swimming 🏊";

		if (client.player.isCrawling())
			return "Crawling 🐛";

		if (client.player.isSneaking())
			return "Sneaking 🚶‍♂️";

		if (client.player.isSprinting())
			return "Sprinting 🏃";

		return String.format("Holding [%s]x%d", client.player.getStackInHand(Hand.MAIN_HAND).getName().getString(),
				client.player.getStackInHand(Hand.MAIN_HAND).getCount());
	}
}
