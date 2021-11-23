package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.List;

public class Shielded extends SBStatusEffect{

    public Shielded(){
        super(StatusEffectCategory.BENEFICIAL, 0x7CB5C6);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    public float onPreArmorDefense(StatusEffectInstance instance, DamageSource source, LivingEntity defender, float amount, List<StatusEffectInstance> effectsToAdd, List<StatusEffect> effectsToRemove){
        if(amount <= 0){
            return amount;
        }
        else if(instance.getAmplifier() == 0){
            effectsToRemove.add(SBStatusEffects.SHIELDED);
        }
        else{
            int shieldDuration = instance.getDuration();
            int shieldAmp = instance.getAmplifier()-1;
            effectsToRemove.add(SBStatusEffects.SHIELDED);
            effectsToAdd.add(new StatusEffectInstance(SBStatusEffects.SHIELDED, shieldDuration, shieldAmp));
        }
        return 0;
    }
}
