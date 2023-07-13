package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RedAlertEnchantment extends SBEnchantment{

    public RedAlertEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.redAlert.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.redAlert.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.redAlert.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.redAlert.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.redAlert.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.redAlert.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.redAlert.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.redAlert.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.redAlert.IS_FOR_SALE;}

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
                    Spellbound.config.redAlert.SHIELD_DURATION+1,
                    redAlertCount-1, false, false, false));
        }
        else if(entity.hasStatusEffect(SBStatusEffects.SHIELDS_DOWN)){
            StatusEffectInstance shields_down = entity.getStatusEffect(SBStatusEffects.SHIELDS_DOWN);
            if(shields_down.getDuration() <= 2){
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0, false, false, false));
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.SHIELDED,
                        Spellbound.config.redAlert.SHIELD_DURATION,
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
            return Spellbound.config.redAlert.RECOVERY_RATE;
        }
        int redAlertLevel = SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getItemsEquipped(), SBEnchantments.RED_ALERT);
        return Math.max(Spellbound.config.redAlert.MINIMUM_RECOVERY_TIME,
                Spellbound.config.redAlert.RECOVERY_RATE -(Spellbound.config.redAlert.RECOVERY_REDUCTION *Math.max(0,redAlertLevel-redAlertCount)/redAlertCount));
    }
}
