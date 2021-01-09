package net.tigereye.spellbound.mob_effect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.enchantments.RedAlertEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import net.tigereye.spellbound.registration.SBConfig;
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
            int redAlert = SBEnchantmentHelper.countEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
            if(redAlert > 0){
                entity.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                        100+ RedAlertEnchantment.getModifiedRecoveryRate(entity,redAlert),
                        0));
            }
        }
    }
}
