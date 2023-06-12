package autumnvn.ass;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASS implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ass");
	public static Boolean died = false;
	public static int deathX = 0;
	public static int deathY = 0;
	public static int deathZ = 0;
	public static String deathWorld = "";
	public static Boolean mobHealth = false;
	private static KeyBinding mobHealthKeybind;

	@Override
	public void onInitialize() {
		mobHealthKeybind = KeyBindingHelper.registerKeyBinding(
				new KeyBinding("ass.mobHealth", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "AutumnVN's silly stuffs"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (mobHealthKeybind.wasPressed()) {
				mobHealth = !mobHealth;
				client.player.sendMessage(
						Text.literal(mobHealth ? "§aMobHealth is enabled" : "§cMobHealth is disabled"), false);
			}
		});
	}
}
