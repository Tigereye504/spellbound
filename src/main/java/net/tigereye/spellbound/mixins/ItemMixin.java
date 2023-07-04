package net.tigereye.spellbound.mixins;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(at = @At("HEAD"), method = "onItemEntityDestroyed")
    public void spellboundOnItemEntityDestroyedMixin(ItemEntity entity, CallbackInfo ci){
        if(entity.getStack().hasEnchantments()){
            SBEnchantmentHelper.onItemDestroyed(entity.getStack(),entity);
            //SBEnchantmentHelper.onInventoryTick((ItemStack)(Object)this, world, entity, slot, selected);
        }
    }
}
