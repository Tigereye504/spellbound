package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PrimingEnchantment extends SBEnchantment{

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
    public void onDoRedHealthDamage(int level, ItemStack itemStack, LivingEntity attacker, LivingEntity victim, DamageSource source, float amount) {
        if(attacker.getWorld().isClient()){
            return;
        }
        if(source.getTypeRegistryEntry().matchesKey(DamageTypes.EXPLOSION)){
            return;
        }
        int effectLevel = 0;
        StatusEffectInstance primedInstance = victim.getStatusEffect(SBStatusEffects.PRIMED);
        if (primedInstance != null) {
            int existingLevel = primedInstance.getAmplifier();
            if(existingLevel >= level) {
                return;
            }
            else{
                effectLevel = existingLevel+1;
            }
        }
        Spellbound.LOGGER.debug("Applying Primed at magnitude " + effectLevel);
        victim.addStatusEffect(new OwnedStatusEffectInstance(attacker, SBStatusEffects.PRIMED, Spellbound.config.priming.DURATION, effectLevel));
    }
}
