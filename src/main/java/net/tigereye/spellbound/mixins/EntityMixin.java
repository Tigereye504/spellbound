package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin{

    @Inject(method = "discard", at = @At("HEAD"))
    private void SpellboundEntityDiscardMixin(CallbackInfo info) {
        if(((Entity)(Object)this) instanceof ItemEntity iEntity && iEntity.getItemAge() >= 6000){
            SBEnchantmentHelper.onItemDestroyed(iEntity.getStack(),iEntity);
        }
    }

    @Inject(method = "tickInVoid", at = @At("HEAD"))
    private void SpellboundEntityTickInVoidMixin(CallbackInfo info) {
        if(((Entity)(Object)this) instanceof ItemEntity iEntity){
            SBEnchantmentHelper.onItemDestroyed(iEntity.getStack(),iEntity);
        }
    }
}
