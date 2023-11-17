package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import static net.tigereye.spellbound.registration.SBStatusEffects.PESTILENCE;

public class PestilenceEffect extends SBStatusEffect implements CustomDataStatusEffect{

    public PestilenceEffect(){
        super(StatusEffectCategory.HARMFUL, 0x194212);
    }

    @Override
    public boolean isInstant() {
        return true;
    }
    @Override
    public void applyInstantEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity) {
        if(attacker instanceof LivingEntity le){
        AtomicInteger longestDuration = new AtomicInteger(1);
        target.getStatusEffects().forEach(effect -> {
            if(effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL) {
                int duration = (int) Math.min(effect.getDuration() * Spellbound.config.pestilence.STATUS_DURATION_FACTOR, Spellbound.config.pestilence.MAX_STATUS_DURATION);
                if (duration > longestDuration.get()) {
                    longestDuration.set(duration);
                }
            }
        });
        target.addStatusEffect(new OwnedStatusEffectInstance(le,PESTILENCE,
                (int)Math.min(longestDuration.get()* Spellbound.config.pestilence.STATUS_DURATION_FACTOR, Spellbound.config.pestilence.MAX_STATUS_DURATION),amplifier));
        }
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        int i = Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY >> amplifier;
        if (i > 0) {
            return duration % i == Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY_OFFSET; //offset with poison to reduce I-frame collision
        }
        return true;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            if(SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.PESTILENCE,entity) == 0) {
                AtomicInteger effectLevels = new AtomicInteger();
                Collection<StatusEffectInstance> effects = entity.getStatusEffects();
                effects.forEach(effect -> {
                    if (effect.getEffectType().getCategory() == StatusEffectCategory.HARMFUL && effect.getEffectType() != PESTILENCE) {
                        effectLevels.addAndGet(Math.min(effect.getAmplifier(), Spellbound.config.pestilence.MAX_DAMAGE_LEVELS_PER_EFFECT - 1) + 1);
                    }
                });
                if (effectLevels.get() > 0) {
                    StatusEffectInstance temp = entity.getStatusEffect(PESTILENCE);
                    if(temp instanceof OwnedStatusEffectInstance si && fillMissingPestilenceData(si,entity)) {
                        entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.PESTILENCE,si.owner),
                                Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
                    }
                    else{
                        entity.damage(SBDamageSources.of(entity.getWorld(),SBDamageSources.PESTILENCE),
                                Spellbound.config.pestilence.DAMAGE_PER_EFFECT * effectLevels.get());
                    }
                }
            }
        }
    }

    public boolean fillMissingPestilenceData(OwnedStatusEffectInstance pestilenceInstance, LivingEntity entity){
        if(pestilenceInstance.owner == null){
            if(pestilenceInstance.ownerUUID == null) {
                return false;
            }
            else{
                ServerWorld world;
                if(entity.getWorld() instanceof ServerWorld){
                    world = (ServerWorld)entity.getWorld();
                }
                else{
                    return false;
                }
                Entity owner = world.getEntity(pestilenceInstance.ownerUUID);
                if(owner instanceof LivingEntity lEntity) {
                    pestilenceInstance.owner = lEntity;
                }
                if(pestilenceInstance.owner == null) {return false;}
            }
        }
        if(pestilenceInstance.ownerUUID == null){
            pestilenceInstance.ownerUUID = pestilenceInstance.owner.getUuid();
        }
        return true;
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return OwnedStatusEffectInstance.customFromNbt(SBStatusEffects.PESTILENCE,tag);
    }
}
