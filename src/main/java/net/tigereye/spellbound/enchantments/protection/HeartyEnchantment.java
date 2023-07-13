package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.UUID;

public class HeartyEnchantment extends SBEnchantment{

    private static final UUID HEARTY_ID = UUID.fromString("94e1b6fd-beb6-4163-9beb-904374c69857");

    public HeartyEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.hearty.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.hearty.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.hearty.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.hearty.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.hearty.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.hearty.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.hearty.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.hearty.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.hearty.IS_FOR_SALE;}

    @Override
    public void onEquipmentChange(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            EntityAttributeModifier mod = new EntityAttributeModifier(HEARTY_ID, "SpellboundHeartyMaxHP",
                    (SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(entity.getItemsEquipped(),SBEnchantments.HEARTY,entity)*Spellbound.config.hearty.HEALTH_FACTOR_PER_LEVEL)+
                            (SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getItemsEquipped(),SBEnchantments.HEARTY,entity)*Spellbound.config.hearty.HEALTH_FACTOR_BASE)
                            ,EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
            ReplaceAttributeModifier(att, mod);
            if(entity.getHealth() > entity.getMaxHealth()){
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }

    private static void ReplaceAttributeModifier(EntityAttributeInstance att, EntityAttributeModifier mod)
    {
        //removes any existing mod and replaces it with the updated one.
        att.removeModifier(mod);
        att.addPersistentModifier(mod);
    }
}
