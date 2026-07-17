package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin{


    @Shadow public abstract Level level();

    @Inject(method = "discard", at = @At("HEAD"))
    private void SpellboundEntityDiscardMixin(CallbackInfo info) {
        if(((Entity)(Object)this) instanceof ItemEntity iEntity && iEntity.getAge() >= 6000){
            SBEnchantmentHelper.onItemDestroyed(iEntity.getItem(),iEntity);
        }
    }

    @Inject(method = "onBelowWorld", at = @At("HEAD"))
    private void SpellboundEntityTickInVoidMixin(CallbackInfo info) {
        if(((Entity)(Object)this) instanceof ItemEntity iEntity){
            SBEnchantmentHelper.onItemDestroyed(iEntity.getItem(),iEntity);
        }
    }
}
