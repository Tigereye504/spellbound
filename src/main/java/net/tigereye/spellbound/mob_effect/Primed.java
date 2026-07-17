package net.tigereye.spellbound.mob_effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;

public class Primed extends SBStatusEffect implements CustomDataStatusEffect{

    public Primed(){
        super(MobEffectCategory.NEUTRAL, 0x194212);
    }


    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!(entity.level().isClientSide)){
            MobEffectInstance temp = entity.getEffect(SBStatusEffects.PRIMED);
            Entity owner = null;
            if(temp instanceof OwnedStatusEffectInstance ti) {
                ti.fillMissingOwnerData(entity);
                owner = ti.owner;
            }
            float range = (amplifier+2)*Spellbound.config.priming.SHOCKWAVE_RADIUS_SCALE;
            SpellboundUtil.psudeoExplosion(owner != null ? owner : entity
                    , Spellbound.config.priming.SAFE_FOR_USER
                    , entity.position()
                    , ((amplifier*amplifier)+1)*Spellbound.config.priming.SHOCKWAVE_DAMAGE_SCALE
                    ,(amplifier+2)*Spellbound.config.priming.SHOCKWAVE_RADIUS_SCALE
                    ,(amplifier+1)*Spellbound.config.priming.SHOCKWAVE_FORCE_SCALE
                    ,range * Spellbound.config.priming.SHOCKWAVE_FULL_DAMAGE_RADIUS
            );
        }
    }

    public void onDeath(MobEffectInstance instance, DamageSource source, LivingEntity defender, List<MobEffectInstance> effectsToAdd, List<MobEffect> effectsToRemove) {
        applyEffectTick(defender,instance.getAmplifier()+1);
    }

    @Override
    public MobEffectInstance getInstanceFromTag(CompoundTag tag) {
        return OwnedStatusEffectInstance.customFromNbt(this, tag);
    }
}
