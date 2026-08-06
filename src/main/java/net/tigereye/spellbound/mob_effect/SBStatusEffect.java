package net.tigereye.spellbound.mob_effect;

import java.util.List;

import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class SBStatusEffect extends MobEffect {

    public SBStatusEffect(MobEffectCategory type, int color) {
        super(type, color);
    }

    //for when the user is struck, before armor is applied
    public float onPreArmorDefense(MobEffectInstance instance, DamageSource source, LivingEntity defender, float amount, List<MobEffectInstance> effectsToAdd, List<Holder<MobEffect>> effectsToRemove){return amount;}

    public void onDeath(MobEffectInstance instance, DamageSource source, LivingEntity defender, List<MobEffectInstance> effectsToAdd, List<Holder<MobEffect>> effectsToRemove) {}
}