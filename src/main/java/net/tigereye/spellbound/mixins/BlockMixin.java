package net.tigereye.spellbound.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class BlockMixin {
    @Inject(at = @At(value="HEAD"), method = "onBreak")
    public void spellboundItemStackGetTooltipMixin(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfo info){
        SBEnchantmentHelper.onBreakBlock((Block)(Object)this, world, pos, state, player);
    }
}
