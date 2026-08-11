package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PrimingEnchantment extends SBEnchantment{

    public PrimingEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE, //enchantment targets: ALL weapons, both melee and ranged
            Spellbound.config.priming.WEIGHT, //enchantment weight
            Spellbound.config.priming.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.priming.BASE_POWER,Spellbound.config.priming.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.priming.BASE_POWER+Spellbound.config.priming.POWER_RANGE,Spellbound.config.priming.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.priming.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }


    @Override
    public boolean isEnabled() {return Spellbound.config.priming.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.priming.SOFT_CAP;}
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
        ((SpellboundLivingEntity)victim).spellbound$setLastPrimer(attacker.getUUID());
        victim.addEffect(new MobEffectInstance(SBStatusEffects.PRIMED, Spellbound.config.priming.DURATION, effectLevel));
    }
}
