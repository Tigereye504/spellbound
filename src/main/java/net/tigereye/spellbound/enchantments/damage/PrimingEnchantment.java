package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PrimingEnchantment extends SBEnchantment{

    private static final Map<UUID,Long> lastUse = new HashMap<>();
    public PrimingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.priming.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.priming.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.priming.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.priming.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.priming.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.priming.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.priming.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.priming.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.priming.IS_FOR_SALE;}

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(user.getWorld().isClient()){
            return;
        }
        if(target instanceof LivingEntity) {
            //minecraft's on target damaged trigger is flawed and calls items in player's main hands twice. Check for that.
            if (lastUse.getOrDefault(user.getUuid(),0L) != user.getWorld().getTime()) {
                lastUse.put(user.getUuid(),user.getWorld().getTime());
                int effectLevel = 0;
                StatusEffectInstance primedInstance = ((LivingEntity) target).getStatusEffect(SBStatusEffects.PRIMED);
                if (primedInstance != null) {
                    effectLevel = Math.min(primedInstance.getAmplifier() + 1, level - 1);
                }
                if (Spellbound.DEBUG) {
                    Spellbound.LOGGER.info("Applying Primed at magnitude " + effectLevel);
                }
                ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(SBStatusEffects.PRIMED, Spellbound.config.priming.DURATION, effectLevel));
            }
        }
        super.onTargetDamaged(user, target, level);
    }
}
