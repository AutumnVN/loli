package autumnvn.ass.mixin;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import autumnvn.ass.ASS;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity> {
	@Shadow
	protected abstract boolean hasLabel(T entity);

	@Shadow
	protected abstract void renderLabelIfPresent(T entity, Text text, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light);

	@Inject(method = "render", at = @At("HEAD"))
	private void onRender(T entity, float yaw, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		if (entity instanceof PlayerEntity || (entity instanceof LivingEntity && ASS.mobHealth)) {
			String entityName = entity.getName().getString();
			int health = (int) Math.ceil(getHealth(entity));
			entityName += "  " + getHealthColor(health) + health + Formatting.RED + " ❤";
			this.renderLabelIfPresent(entity, Text.of(entityName), matrices, vertexConsumers, light);
		}
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
}
