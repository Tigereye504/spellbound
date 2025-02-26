package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

public class Bravados extends SBStatusEffect{

    public Bravados(){
        super(StatusEffectCategory.NEUTRAL, 0x194212);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }


    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(entity.getAbsorptionAmount() == 0){
            entity.removeStatusEffect(SBStatusEffects.BRAVADOS);
        }
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        entity.setAbsorptionAmount(0);
        SBEnchantmentHelper.onRedHealthDamage(entity.getDamageSources().generic(),entity,0);
    }
}
