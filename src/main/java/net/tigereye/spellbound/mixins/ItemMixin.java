package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "onDestroyed")
    public void spellboundOnItemEntityDestroyedMixin(ItemEntity entity, CallbackInfo ci){
        if(entity.getItem().isEnchanted()){
            SBEnchantmentHelper.onItemDestroyed(entity.getItem(),entity);
            //SBEnchantmentHelper.onInventoryTick((ItemStack)(Object)this, world, entity, slot, selected);
        }
    }

    @Inject(at = @At("RETURN"), method = "getDestroySpeed", cancellable = true)
    public void spellboundItemGetDestroySpeedMixin(ItemStack itemStack, BlockState blockState, CallbackInfoReturnable<Float> cir){
        if(itemStack.isEnchanted()){
            cir.setReturnValue(SBEnchantmentHelper.getBaseMiningSpeed(itemStack, blockState, cir.getReturnValue()));
        }
    }

    @Inject(at = @At("RETURN"), method = "isCorrectToolForDrops", cancellable = true)
    public void spellboundItemisCorrectToolForDropsMixin(ItemStack itemStack, BlockState blockState, CallbackInfoReturnable<Boolean> cir){
        if(itemStack.isEnchanted()){
            cir.setReturnValue(SBEnchantmentHelper.setItemSuitability(itemStack, blockState, cir.getReturnValue()));
        }
    }
}
