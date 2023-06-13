package autumnvn.ass.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import autumnvn.ass.ASS;
import net.minecraft.client.font.TextRenderer;
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
public abstract class EntityRendererMixin<T extends Entity> {
	@Shadow
	protected abstract boolean hasLabel(T entity);

	@Shadow
	protected abstract void renderLabelIfPresent(T entity, Text text, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light);

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void onRender(T entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (entity instanceof PlayerEntity || (entity instanceof LivingEntity && ASS.mobHealth)) {
			String entityName = entity.getName().getString();
			int health = (int) Math.ceil(getHealth(entity));
			entityName += "  " + getHealthColor(health) + health + Formatting.RED + " ❤";

			if (entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative())
				entityName += Formatting.RESET + " [C]";

			if (entity instanceof HorseEntity && ASS.mobHealth) {
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

	@ModifyArgs(method = "renderLabelIfPresent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/text/Text;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)I"))
	private void modify(Args args) {
		args.set(3, 0xFFFFFFFF);
		args.set(7, TextRenderer.TextLayerType.SEE_THROUGH);
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
