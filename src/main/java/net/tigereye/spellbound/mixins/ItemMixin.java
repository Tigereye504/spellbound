package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "onDestroyed")
    public void spellboundOnItemEntityDestroyedMixin(ItemEntity entity, CallbackInfo ci){
        if(entity.getItem().isEnchanted()){
            SBEnchantmentHelper.onItemDestroyed(entity.getItem(),entity);
            //SBEnchantmentHelper.onInventoryTick((ItemStack)(Object)this, world, entity, slot, selected);
        }
    }
}
