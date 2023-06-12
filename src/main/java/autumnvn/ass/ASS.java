package autumnvn.ass;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ASS implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("ass");
	public static Boolean died = false;
	public static int deathX = 0;
	public static int deathY = 0;
	public static int deathZ = 0;
	public static String deathWorld = "";

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
	}
}
