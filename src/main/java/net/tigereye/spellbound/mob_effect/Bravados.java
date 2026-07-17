package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

public class Bravados extends SBStatusEffect{

    public Bravados(){
        super(MobEffectCategory.NEUTRAL, 0x194212);
    }


    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }


    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.getAbsorptionAmount() == 0){
            entity.removeEffect(SBStatusEffects.BRAVADOS);
        }
    }

    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        entity.setAbsorptionAmount(0);
        SBEnchantmentHelper.onRedHealthDamage(entity.damageSources().generic(),entity,0);
    }
}
