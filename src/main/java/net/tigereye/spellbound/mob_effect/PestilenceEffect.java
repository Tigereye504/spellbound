package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tigereye.spellbound.registration.SBStatusEffects.PESTILENCE;

public class PestilenceEffect extends SBStatusEffect{

    public PestilenceEffect(){
        super(MobEffectCategory.HARMFUL, 0x194212);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int i = Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY >> amplifier;
        if (i > 1) {
            return duration % i == Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY_OFFSET; //offset with poison to reduce I-frame collision
        }
        return true;
    }
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if(!(entity.level().isClientSide)){
            //first, check if the status is owned by the victim. If so, they are immune.
            //Entity owner = null;
            //MobEffectInstance instance = entity.getEffect(PESTILENCE);
            //if(instance instanceof OwnedStatusEffectInstance si && si.fillMissingOwnerData(entity)) {
            //    if(si.owner == entity) return true;
            //    else owner = si.owner;
            //}
            //TODO: for now, as the ownership check isn't working correctly on servers, we will simply make all pestilence users immune to pestilence
            if(SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.PESTILENCE,entity) > 0){
                return true;
            }

            //tally up the levels of negative effects on the target
            AtomicInteger effectLevels = new AtomicInteger();
            Collection<MobEffectInstance> effects = entity.getActiveEffects();
            effects.forEach(effect -> {
                if (effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && effect.getEffect() != PESTILENCE) {
                    effectLevels.addAndGet(Math.min(effect.getAmplifier(), Spellbound.config.pestilence.MAX_DAMAGE_LEVELS_PER_EFFECT - 1) + 1);
                }
            });

            //do damage based on the negitive effect count.
            //if(owner != null) {
            //    entity.hurt(SBDamageSources.of(entity.level(),SBDamageSources.PESTILENCE,owner),
            //            Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
            //}
            //else{
                entity.hurt(SBDamageSources.of(entity.level(),SBDamageSources.PESTILENCE),
                        Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
            //}
        }
        return true;
    }


}
