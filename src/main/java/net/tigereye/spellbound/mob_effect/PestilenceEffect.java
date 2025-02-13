package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tigereye.spellbound.registration.SBStatusEffects.PESTILENCE;

public class PestilenceEffect extends SBStatusEffect implements CustomDataStatusEffect{

    public PestilenceEffect(){
        super(StatusEffectCategory.HARMFUL, 0x194212);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY >> amplifier;
        if (i > 1) {
            return duration % i == Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY_OFFSET; //offset with poison to reduce I-frame collision
        }
        return true;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            //first, check if the status is owned by the victim. If so, they are immune.
            Entity owner = null;
            StatusEffectInstance instance = entity.getStatusEffect(PESTILENCE);
            if(instance instanceof OwnedStatusEffectInstance si && si.fillMissingOwnerData(entity)) {
                if(si.owner == entity) return;
                else owner = si.owner;
            }
            //TODO: for now, as the ownership check isn't working correctly on servers, we will simply make all pestilence users immune to pestilence
            if(SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.PESTILENCE,entity) > 0){
                return;
            }

            //tally up the levels of negative effects on the target
            AtomicInteger effectLevels = new AtomicInteger();
            Collection<StatusEffectInstance> effects = entity.getStatusEffects();
            effects.forEach(effect -> {
                if (effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL && effect.getEffectType() != PESTILENCE) {
                    effectLevels.addAndGet(Math.min(effect.getAmplifier(), Spellbound.config.pestilence.MAX_DAMAGE_LEVELS_PER_EFFECT - 1) + 1);
                }
            });

            //do damage based on the negitive effect count.
            if(owner != null) {
                entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.PESTILENCE,owner),
                        Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
            }
            else{
                entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.PESTILENCE),
                        Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
            }
        }
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return OwnedStatusEffectInstance.customFromNbt(SBStatusEffects.PESTILENCE,tag);
    }


}
