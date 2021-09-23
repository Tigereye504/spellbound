package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.Iterator;
import java.util.Map;

public class Bravados extends SBStatusEffect{

    public Bravados(){
        super(StatusEffectType.NEUTRAL, 0x194212);
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
    }
}
