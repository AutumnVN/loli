package autumnvn.ass.command;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TPS {

    private static long lastTick = -1;
    private static long lastUpdate = -1;
    public static double tps = -1;

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("tps").executes(context -> {
            MinecraftClient client = context.getSource().getClient();
            client.player.sendMessage(Text.literal(String.format("%sTPS: %.2f", getTpsColor(tps), tps)), false);
            return 1;
        }));
    }

    private static Formatting getTpsColor(double tps) {
        if (tps > 18)
            return Formatting.GREEN;

        if (tps > 16)
            return Formatting.YELLOW;

        return Formatting.RED;
    }

    public static void updateTime(long ticks) {
        if (lastTick < 0) {
            lastTick = ticks;
            lastUpdate = System.nanoTime();
            return;
        }

        long time = System.nanoTime();
        double elapsedMilli = (time - lastUpdate) / 1000000d;
        int passedTicks = (int) (ticks - lastTick);

        if (passedTicks > 0) {
            double mspt = elapsedMilli / passedTicks;
            tps = Math.min(1000 / mspt, 20);
        }

        lastTick = ticks;
        lastUpdate = time;
    }
}
