package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.protection.RedAlertEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class ShieldsDown extends SBStatusEffect{

    public ShieldsDown(){
        super(MobEffectCategory.NEUTRAL, 0x375159);
    } //7CB5C6 for shields up


    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!(entity.level().isClientSide)){
            int redAlert = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getAllSlots(), SBEnchantments.RED_ALERT);
            if(redAlert > 0){
                entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDED,
                        Spellbound.config.redAlert.SHIELD_DURATION + RedAlertEnchantment.getModifiedRecoveryRate(entity,redAlert),
                        0, false, false, true));
            }
            else{
                entity.removeEffect(SBStatusEffects.SHIELDED);
            }
        }
    }
}
