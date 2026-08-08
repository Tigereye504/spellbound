package net.tigereye.spellbound.mob_effect;

import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.UUID;

public class Primed extends SBStatusEffect{

    public static final String OWNER_KEY = Spellbound.MODID+":primed_owner";

    public Primed(){
        super(MobEffectCategory.NEUTRAL, 0x194212);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration <= 1;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(entity.level() instanceof ServerLevel sLevel){
            UUID ownerUuid = ((SpellboundLivingEntity) entity).spellbound$getLastPrimer();
            Entity owner = sLevel.getEntity(ownerUuid);
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
        ((SpellboundLivingEntity) entity).spellbound$setLastPrimer(null);
        return true;
    }

    @Override
    public void onDeath(MobEffectInstance instance, DamageSource source, LivingEntity defender, List<MobEffectInstance> effectsToAdd, List<Holder<MobEffect>> effectsToRemove) {
        applyEffectTick(defender,instance.getAmplifier()+1);
    }
}
