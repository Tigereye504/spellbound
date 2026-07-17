package net.tigereye.spellbound.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At(value="HEAD"), method = "playerWillDestroy")
    public void spellboundBlockOnBreakMixin(Level world, BlockPos pos, BlockState state, Player player, CallbackInfo info){
        SBEnchantmentHelper.onBreakBlockDirectly((Block)(Object)this, world, pos, state, player);
    }
}
