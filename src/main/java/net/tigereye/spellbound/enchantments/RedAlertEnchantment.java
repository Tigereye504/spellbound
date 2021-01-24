package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.tigereye.spellbound.mob_effect.Shielded;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.List;

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
        return isAcceptableAtTable(stack);
    }

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity, List<StatusEffectInstance> effectsToAdd, List<StatusEffect> effectsToRemove){
        if(entity.hasStatusEffect(SBStatusEffects.SHIELDED)){
            StatusEffectInstance shielded = entity.getStatusEffect(SBStatusEffects.SHIELDED);
            if(shielded != null && shielded.getDuration() <= SBConfig.SHIELD_DURATION_OFFSET){
                int redAlertCount = SBEnchantmentHelper.countEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
                if(redAlertCount > 0){
                    effectsToRemove.add(SBStatusEffects.SHIELDED);
                    effectsToAdd.add(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                            SBConfig.SHIELD_DURATION_OFFSET+ RedAlertEnchantment.getModifiedRecoveryRate(entity,redAlertCount),
                            Math.min(redAlertCount-1,shielded.getAmplifier()+1)));
                }
            }
        }
        else if(!entity.hasStatusEffect(SBStatusEffects.SHIELDS_DOWN)) {
            effectsToAdd.add(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0));
        }
    }

    public boolean canAccept(Enchantment other) {
        return !(other instanceof ProtectionEnchantment) && super.canAccept(other);
    }

    public static int getModifiedRecoveryRate(LivingEntity entity){
        return getModifiedRecoveryRate(entity,SBEnchantmentHelper.countEnchantmentInstances(entity.getItemsEquipped(),SBEnchantments.RED_ALERT));
    }
    public static int getModifiedRecoveryRate(LivingEntity entity, int redAlertCount){
        if(redAlertCount == 0){
            return SBConfig.SHIELD_RECOVERY_RATE;
        }
        int redAlertLevel = SBEnchantmentHelper.getEnchantmentAmount(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
        return Math.max(SBConfig.MINIMUM_SHIELD_RECOVERY_TIME,
                SBConfig.SHIELD_RECOVERY_RATE-(SBConfig.SHIELD_RECOVERY_REDUCTION*Math.max(0,redAlertLevel-redAlertCount)/redAlertCount));
    }

    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }
}
