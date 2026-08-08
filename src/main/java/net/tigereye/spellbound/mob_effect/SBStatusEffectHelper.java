package net.tigereye.spellbound.mob_effect;

import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class SBStatusEffectHelper {



    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBStatusEffectHelper.forEachStatusEffect((instance,effectsToAdd,effectsToRemove) -> {
            if(instance.getEffect().value() instanceof SBStatusEffect effect) {
                mutableFloat.setValue(effect.onPreArmorDefense(instance, source, defender, mutableFloat.floatValue(), effectsToAdd, effectsToRemove));
            }
        }, defender.getActiveEffects(), defender);
        return mutableFloat.floatValue();
    }

    public static void onDeath(DamageSource source, LivingEntity defender){
        SBStatusEffectHelper.forEachStatusEffect((instance,effectsToAdd,effectsToRemove) -> {
            if(instance.getEffect().value() instanceof SBStatusEffect effect) {
                effect.onDeath(instance, source, defender, effectsToAdd, effectsToRemove);
            }
        }, defender.getActiveEffects(), defender);
    }

    private static void forEachStatusEffect(SBStatusEffectHelper.Consumer consumer, Collection<MobEffectInstance> effects, LivingEntity entity) {
        List<MobEffectInstance> effectsToAdd = new ArrayList<>();
        List<Holder<MobEffect>> effectsToRemove = new ArrayList<>();
        for (MobEffectInstance effect:
             effects) {
            consumer.accept(effect,effectsToAdd,effectsToRemove);
        }
        for (Holder<MobEffect> effect:
                effectsToRemove) {
            entity.removeEffect(effect);
        }
        for (MobEffectInstance effect:
                effectsToAdd) {
            entity.addEffect(effect);
        }
    }

    @FunctionalInterface
    interface Consumer {
        void accept(MobEffectInstance instance, List<MobEffectInstance> effectsToAdd, List<Holder<MobEffect>> effectsToRemove);
    }
}
