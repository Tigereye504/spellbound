package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

public class RedAlertEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public RedAlertEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public int getMinPower(int level) {
        return (level*11)-10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.RED_ALERT_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 4;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
            return;
        }
        if(entity.hasStatusEffect(SBStatusEffects.SHIELDS_DOWN)){
            StatusEffectInstance shields_down = entity.getStatusEffect(SBStatusEffects.SHIELDS_DOWN);
            if(shields_down.getDuration() <= 2){
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0, false, false, false));

                //refresh shielded
                if(entity.hasStatusEffect(SBStatusEffects.SHIELDED)){
                    StatusEffectInstance shielded = entity.getStatusEffect(SBStatusEffects.SHIELDED);
                    int redAlertCount = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
                    if(redAlertCount > 0){
                        //entity.removeStatusEffect(SBStatusEffects.SHIELDED);
                        entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                                Spellbound.config.RED_ALERT_SHIELD_DURATION,
                                Math.min(redAlertCount-1,shielded.getAmplifier()+1), false, false, true));
                    }
                }
                else{
                    entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,Spellbound.config.RED_ALERT_SHIELD_DURATION,0, false, false, true));
                }
            }
        }
        else{
            entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0, false, false, false));
        }


    }

    public static int getModifiedRecoveryRate(LivingEntity entity){
        return getModifiedRecoveryRate(entity,SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(),SBEnchantments.RED_ALERT));
    }
    public static int getModifiedRecoveryRate(LivingEntity entity, int redAlertCount){
        if(redAlertCount == 0){
            return Spellbound.config.RED_ALERT_RECOVERY_RATE;
        }
        int redAlertLevel = SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
        return Math.max(Spellbound.config.RED_ALERT_MINIMUM_RECOVERY_TIME,
                Spellbound.config.RED_ALERT_RECOVERY_RATE -(Spellbound.config.RED_ALERT_RECOVERY_REDUCTION *Math.max(0,redAlertLevel-redAlertCount)/redAlertCount));
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }
}
