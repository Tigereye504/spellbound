package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.protection.RedAlertEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class ShieldsDown extends SBStatusEffect{

    public ShieldsDown(){
        super(StatusEffectCategory.NEUTRAL, 0x375159);
    } //7CB5C6 for shields up


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            int redAlert = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
            if(redAlert > 0){
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                        Spellbound.config.redAlert.SHIELD_DURATION + RedAlertEnchantment.getModifiedRecoveryRate(entity,redAlert),
                        0, false, false, true));
            }
            else{
                entity.removeStatusEffect(SBStatusEffects.SHIELDED);
            }
        }
    }
}
