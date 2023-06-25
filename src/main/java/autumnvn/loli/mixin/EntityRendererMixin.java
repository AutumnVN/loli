package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin<T extends Entity> {

	@Shadow
	protected boolean hasLabel(T entity) {
		return false;
	}

	@Shadow
	protected void renderLabelIfPresent(T entity, Text text, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light) {
	}

	// PlayerHealth, MobHealth, HorseStats, PlayerGamemode
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(T entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (entity instanceof LivingEntity) {
			String entityName = entity.getName().getString();
			int health = (int) Math.ceil(getHealth(entity));
			entityName += "  " + getHealthColor(health) + health + Formatting.RED + " ❤";

			if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative())
				entityName += Formatting.RESET + " [C]";

			if (entity instanceof HorseEntity) {
				double speed = ((HorseEntity) entity).getAttributes()
						.getValue(EntityAttributes.GENERIC_MOVEMENT_SPEED) * 42.157787584D;
				double jumpStrength = ((HorseEntity) entity).getJumpStrength();
				double jumpHeight = -0.1817584952D * jumpStrength * jumpStrength * jumpStrength
						+ 3.689713992D * jumpStrength * jumpStrength + 2.128599134D * jumpStrength - 0.343930367D;
				entityName += getHorseColor(4.742751103D, 14.228253309D, speed)
						+ String.format("  %.1f ➡", speed)
						+ getHorseColor(1.08623D, 5.29262D, jumpHeight)
						+ String.format("  %.1f ⬆", jumpHeight);
			}

			this.renderLabelIfPresent(entity, Text.of(entityName), matrices, vertexConsumers, light);
			ci.cancel();
		}
	}

	// VisibleName
	@ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
	private void onDraw(Args args) {
		args.set(3, 0xffffffff);
	}

	@ModifyVariable(method = "renderLabelIfPresent", at = @At("STORE"), ordinal = 0)
	private boolean bl(boolean bl) {
		return true;
	}

	private float getHealth(T entity) {
		return ((LivingEntity) entity).getHealth() + ((LivingEntity) entity).getAbsorptionAmount();
	}

	private Formatting getHealthColor(int health) {
		if (health <= 5)
			return Formatting.RED;

		if (health <= 10)
			return Formatting.GOLD;

		if (health <= 15)
			return Formatting.YELLOW;

		if (health <= 20)
			return Formatting.GREEN;

		return Formatting.DARK_GREEN;
	}

	private Formatting getHorseColor(double min, double max, double value) {
		double third = (max - min) / 3.0D;
		if (value + 2 * third < max)
			return Formatting.RED;

		if (value + third < max)
			return Formatting.GOLD;

		return Formatting.GREEN;

	}
}
