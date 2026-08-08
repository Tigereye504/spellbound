package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RedAlertEnchantment extends SBEnchantment{

    public RedAlertEnchantment() {
        super(definition(Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS ? SBTags.ARMOR_AND_SHIELD_ENCHANTABLE : ItemTags.ARMOR_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.redAlert.RARITY), //enchantment weight
            Spellbound.config.redAlert.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.redAlert.BASE_POWER,Spellbound.config.redAlert.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.redAlert.BASE_POWER+Spellbound.config.redAlert.POWER_RANGE,Spellbound.config.redAlert.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.redAlert.RARITY-1), //level cost at anvil
            Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.redAlert.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.redAlert.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.redAlert.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.redAlert.IS_FOR_SALE;}

    @Override
    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        //only bother if Red Alert count changed. If they didn't, things can carry on as before.
        if((oldLevel == 0 && newLevel == 0) || (oldLevel != 0 && newLevel != 0)){
            return;
        }

        updateShieldStatusEffects(entity);
    }

    @Override
    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        updateShieldStatusEffects(defender);
        return amount;
    }

    @Override
    public void onStatusEffectsCleared(int level, ItemStack itemStack, LivingEntity owner) {
        updateShieldStatusEffects(owner);
    }

    private void updateShieldStatusEffects(LivingEntity entity){
        //Check the player's shielded magnitude.
        //   if magnitude is higher than count: 
        //      if duration is infinite:
        //          lower the magnitude to the new Red Alert count.
        //          remove ongoing shield recovery.
        //      else:
        //          start shield recovery by applying 'Shields Down'.
        //   elseif magnitude is lower than count: 
        //      start shield recovery by applying 'Shields Down'.
        int redAlertCount = SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getAllSlots(), SBEnchantments.RED_ALERT,entity);
        MobEffectInstance shieldedInstance = null;
        int shieldedLevel = 0;
        if(entity.hasEffect(SBStatusEffects.SHIELDED)){
            shieldedInstance = entity.getEffect(SBStatusEffects.SHIELDED);
            shieldedLevel = shieldedInstance.getAmplifier()+1;
        }

        if(shieldedLevel > redAlertCount){
            if(shieldedInstance.isInfiniteDuration()){
                entity.removeEffect(SBStatusEffects.SHIELDED);
                entity.removeEffect(SBStatusEffects.SHIELDS_DOWN);
                if(redAlertCount > 0){
                entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDED,
                    MobEffectInstance.INFINITE_DURATION,
                    redAlertCount-1, false, false, false));
                }
            }
            else if(redAlertCount > 0){
                startShieldRecovery(entity,redAlertCount-1);
            }
        }
        else if(shieldedLevel < redAlertCount){
            startShieldRecovery(entity,redAlertCount-1);
        }
    }

    private void startShieldRecovery(LivingEntity entity, int magnitude){
        entity.removeEffect(SBStatusEffects.SHIELDS_DOWN);
        entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDS_DOWN,
                    getModifiedRecoveryRate(entity),
                    magnitude, false, false, false));
    }
 
    public static int getModifiedRecoveryRate(LivingEntity entity){
        int redAlertSurplus = SBEnchantmentHelper.getSpellboundEnchantmentAmount(entity.getAllSlots(), SBEnchantments.RED_ALERT)
                                - SBEnchantmentHelper.countSpellboundEnchantmentInstances(entity.getAllSlots(),SBEnchantments.RED_ALERT);
        float d = 12f/((((float)Spellbound.config.redAlert.MAXIMUM_RECOVERY_TIME-Spellbound.config.redAlert.MINIMUM_RECOVERY_TIME)
                        /(Spellbound.config.redAlert.FULL_SET_RECOVERY_TIME-Spellbound.config.redAlert.MINIMUM_RECOVERY_TIME))-1);
        float factor = 1-(redAlertSurplus / (redAlertSurplus+d));
        return Spellbound.config.redAlert.MINIMUM_RECOVERY_TIME + (int)((Spellbound.config.redAlert.MAXIMUM_RECOVERY_TIME-Spellbound.config.redAlert.MINIMUM_RECOVERY_TIME) * factor);
    }
}
