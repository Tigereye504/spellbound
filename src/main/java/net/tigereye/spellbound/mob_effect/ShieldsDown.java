package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.RedAlertEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class ShieldsDown extends SBStatusEffect{

    public ShieldsDown(){
        super(StatusEffectType.NEUTRAL, 0x375159);
    } //7CB5C6 for shields up


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.world.isClient)){
            int redAlert = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
            if(redAlert > 0){
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                        Spellbound.config.SHIELD_DURATION + RedAlertEnchantment.getModifiedRecoveryRate(entity,redAlert),
                        0, false, false, true));
            }
        }
    }

    public static StatusEffectInstance generateInstance(LivingEntity entity, int duration){
        int redAlert = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
        if(redAlert > 0) {
            int mrr = RedAlertEnchantment.getModifiedRecoveryRate(entity, redAlert);
            return new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN,
                    Math.min(Spellbound.config.SHIELD_DURATION + mrr, duration),
                    0);
        }
        return null;
    }
}
