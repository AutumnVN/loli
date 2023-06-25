package autumnvn.loli.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

@Mixin(BarrierBlock.class)
public class BarrierBlockMixin {

    // VisibleBarrier
    @Inject(method = "getRenderType", at = @At("HEAD"), cancellable = true)
    public void onGetRenderType(CallbackInfoReturnable<BlockRenderType> cir) {
        cir.setReturnValue(BlockRenderType.MODEL);
    }

    public boolean isSideInvisible(BlockState state, BlockState stateFrom, Direction direction) {
        if (stateFrom.isOf(Blocks.BARRIER))
            return true;
        return false;
    }
}
