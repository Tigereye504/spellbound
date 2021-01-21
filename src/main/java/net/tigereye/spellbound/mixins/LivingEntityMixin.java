package net.tigereye.spellbound.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import net.tigereye.spellbound.mob_effect.SBStatusEffect;
import net.tigereye.spellbound.mob_effect.SBStatusEffectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @ModifyVariable(at = @At("HEAD"), ordinal = 0, method = "applyArmorToDamage")
    public float spellboundLivingEntityApplyArmorMixin(float amount, DamageSource source){
        amount = SBEnchantmentHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
        return SBStatusEffectHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
    }

    //Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(
    //  Ljava/lang/Iterable;
    //  Lnet/minecraft/entity/damage/DamageSource;
    //)I
    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I"), ordinal = 0, method = "applyEnchantmentsToDamage")
    public int spellboundLivingEntityApplyEnchantmentsToDamageMixin(int k, DamageSource source, float amount){
        return SBEnchantmentHelper.getProtectionAmount(source,(LivingEntity)(Object)this,k,amount);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void spellboundLivingEntityTickMixin(CallbackInfo info){
        SBEnchantmentHelper.onTickWhileEquipped((LivingEntity)(Object)this);
    }

    @Inject(at = @At("HEAD"), method = "onKilledBy")
    public void spellboundLivingEntityOnKilledByMixin(LivingEntity adversary, CallbackInfo info){
        if(adversary != null) {
            SBEnchantmentHelper.onDeath(adversary,(LivingEntity) (Object) this);
        }
    }
}
