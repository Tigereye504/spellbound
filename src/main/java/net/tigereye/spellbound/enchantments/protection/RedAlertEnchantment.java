package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
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
    public boolean isTreasureOnly() {return Spellbound.config.redAlert.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.redAlert.IS_FOR_SALE;}

    @Override
    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(stack)) != stack){
            return;
        }
        int shieldedLevel = 0;
        if(entity.hasEffect(SBStatusEffects.SHIELDED)){
            shieldedLevel = entity.getEffect(SBStatusEffects.SHIELDED).getAmplifier()+1;
        }
        int redAlertCount = SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getAllSlots(), SBEnchantments.RED_ALERT);
        if(redAlertCount <= shieldedLevel){
            entity.removeEffect(SBStatusEffects.SHIELDS_DOWN);
            entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDED,
                    Spellbound.config.redAlert.SHIELD_DURATION+1,
                    redAlertCount-1, false, false, false));
        }
        else if(entity.hasEffect(SBStatusEffects.SHIELDS_DOWN)){
            MobEffectInstance shields_down = entity.getEffect(SBStatusEffects.SHIELDS_DOWN);
            if(shields_down.getDuration() <= 2){
                entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0, false, false, false));
                entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDED,
                        Spellbound.config.redAlert.SHIELD_DURATION,
                        Math.min(redAlertCount-1,shieldedLevel), false, false, false));
            }
        }
        else{
            entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDS_DOWN, RedAlertEnchantment.getModifiedRecoveryRate(entity), 0, false, false, false));
        }
    }

    public static int getModifiedRecoveryRate(LivingEntity entity){
        return getModifiedRecoveryRate(entity,SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getAllSlots(),SBEnchantments.RED_ALERT));
    }
    public static int getModifiedRecoveryRate(LivingEntity entity, int redAlertCount){
        if(redAlertCount == 0){
            return Spellbound.config.redAlert.RECOVERY_RATE;
        }
        int redAlertLevel = SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getAllSlots(), SBEnchantments.RED_ALERT);
        return Math.max(Spellbound.config.redAlert.MINIMUM_RECOVERY_TIME,
                Spellbound.config.redAlert.RECOVERY_RATE -(Spellbound.config.redAlert.RECOVERY_REDUCTION *Math.max(0,redAlertLevel-redAlertCount)/redAlertCount));
    }
}
