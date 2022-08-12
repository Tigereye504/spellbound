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
import net.tigereye.spellbound.mob_effect.SBStatusEffect;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RedAlertEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public RedAlertEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.RED_ALERT_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.RED_ALERT_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.RED_ALERT_POWER_PER_RANK * level) - Spellbound.config.RED_ALERT_BASE_POWER;
        if(level > Spellbound.config.RED_ALERT_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.RED_ALERT_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.RED_ALERT_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
            return;
        }
        int shieldedLevel = 0;
        if(entity.hasStatusEffect(SBStatusEffects.SHIELDED)){
            shieldedLevel = entity.getStatusEffect(SBStatusEffects.SHIELDED).getAmplifier()+1;
        }
        int redAlertCount = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
        if(redAlertCount <= shieldedLevel){
            entity.removeStatusEffect(SBStatusEffects.SHIELDS_DOWN);
            entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                    Spellbound.config.RED_ALERT_SHIELD_DURATION+1,
                    redAlertCount-1, false, false, false));
        }
        else if(entity.hasStatusEffect(SBStatusEffects.SHIELDS_DOWN)){
            StatusEffectInstance shields_down = entity.getStatusEffect(SBStatusEffects.SHIELDS_DOWN);
            if(shields_down.getDuration() <= 2){
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0, false, false, false));
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                        Spellbound.config.RED_ALERT_SHIELD_DURATION,
                        Math.min(redAlertCount-1,shieldedLevel), false, false, false));
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
