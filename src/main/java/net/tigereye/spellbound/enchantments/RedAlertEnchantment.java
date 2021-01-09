package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class RedAlertEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public RedAlertEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
    }

    public int getMinPower(int level) {
        return (level*11)-10;
    }

    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    public int getMaxLevel() {
        return 4;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) || isAcceptableAtTable(stack);
    }

    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(amount <= 0){
            return amount;
        }
        StatusEffectInstance shields = defender.getStatusEffect(SBStatusEffects.SHIELDED);
        if(shields != null){
            if(shields.getAmplifier() == 0){
                defender.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN,
                        shields.getDuration()-100,
                        0));
                defender.removeStatusEffect(SBStatusEffects.SHIELDED);
            }
            else{
                int shieldDuration = shields.getDuration();
                int shieldAmp = shields.getAmplifier()-1;
                defender.removeStatusEffect(SBStatusEffects.SHIELDED);
                defender.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED, shieldDuration, shieldAmp));
            }
            return 0;
        }
        if(!defender.hasStatusEffect(SBStatusEffects.SHIELDS_DOWN)) {
            defender.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN,
                    RedAlertEnchantment.getModifiedRecoveryRate(defender),
                    0));
        }
        return amount;
    }

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(!entity.hasStatusEffect(SBStatusEffects.SHIELDS_DOWN) && !entity.hasStatusEffect(SBStatusEffects.SHIELDED))
        entity.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0));
    }

    public boolean canAccept(Enchantment other) {
        return !(other instanceof ProtectionEnchantment) && super.canAccept(other);
    }

    public static int getModifiedRecoveryRate(LivingEntity entity){
        return getModifiedRecoveryRate(entity,SBEnchantmentHelper.countEnchantmentInstances(entity.getItemsEquipped(),SBEnchantments.RED_ALERT));
    }
    public static int getModifiedRecoveryRate(LivingEntity entity, int redAlertCount){
        int redAlertLevel = SBEnchantmentHelper.getEnchantmentAmount(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
        int modifiedRecoveryRate = Math.max(SBConfig.MINIMUM_SHIELD_RECOVERY_TIME,
                SBConfig.SHIELD_RECOVERY_RATE-(SBConfig.SHIELD_RECOVERY_REDUCTION*Math.max(0,redAlertLevel-redAlertCount)/redAlertCount));
        return modifiedRecoveryRate;
    }

    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }
}
