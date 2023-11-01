package net.tigereye.spellbound.mixins;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin{

    @Inject(at = @At("RETURN"), method = "getLooting", cancellable = true)
    private static void spellboundGetLootingMixin(LivingEntity entity, CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(cir.getReturnValue() + SBEnchantmentHelper.getLooting(entity));
    }
}
