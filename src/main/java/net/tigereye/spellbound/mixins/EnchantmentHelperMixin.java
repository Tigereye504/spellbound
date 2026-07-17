package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin{

    @Inject(at = @At("RETURN"), method = "getMobLooting", cancellable = true)
    private static void spellboundGetLootingMixin(LivingEntity entity, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(cir.getReturnValue() + SBEnchantmentHelper.getLooting(entity));
    }

    @Inject(at = @At("RETURN"), method = "doPostDamageEffects")
    private static void spellboundOnTargetDamagedMixin(LivingEntity user, Entity target, CallbackInfo ci){
        if(user != null) {
            SBEnchantmentHelper.onTargetDamaged(user, target);
        }
    }
}
