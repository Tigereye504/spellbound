package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
    public boolean isTreasureOnly() {return Spellbound.config.priming.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.priming.IS_FOR_SALE;}

    @Override
    public void onDoRedHealthDamage(int level, ItemStack itemStack, LivingEntity attacker, LivingEntity victim, DamageSource source, float amount) {
        if(attacker.level().isClientSide()){
            return;
        }
        if(source.typeHolder().is(DamageTypes.EXPLOSION)){
            return;
        }
        int effectLevel = 0;
        MobEffectInstance primedInstance = victim.getEffect(SBStatusEffects.PRIMED);
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
        victim.addEffect(new OwnedStatusEffectInstance(attacker, SBStatusEffects.PRIMED, Spellbound.config.priming.DURATION, effectLevel));
    }
}
