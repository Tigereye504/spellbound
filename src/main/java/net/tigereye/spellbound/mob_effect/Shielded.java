package net.tigereye.spellbound.mob_effect;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.tigereye.spellbound.enchantments.RedAlertEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class Shielded extends SBStatusEffect{

    public Shielded(){
        super(StatusEffectType.BENEFICIAL, 0x7CB5C6);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 100;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.world.isClient)){
            int redAlertCount = SBEnchantmentHelper.countEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
            if(redAlertCount > 0){
                entity.removeStatusEffect(SBStatusEffects.SHIELDED);
                entity.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                        100+ RedAlertEnchantment.getModifiedRecoveryRate(entity,redAlertCount),
                        Math.min(redAlertCount-1,amplifier+1)));
            }
        }
    }
}
