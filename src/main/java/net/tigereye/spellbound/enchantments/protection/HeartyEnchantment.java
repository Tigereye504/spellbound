package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
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
    public boolean isTreasureOnly() {return Spellbound.config.hearty.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.hearty.IS_FOR_SALE;}

    @Override
    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        AttributeInstance att = entity.getAttribute(Attributes.MAX_HEALTH);
        if(att != null) {
            AttributeModifier mod = new AttributeModifier(HEARTY_ID, "SpellboundHeartyMaxHP",
                    (SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(entity.getAllSlots(),SBEnchantments.HEARTY,entity)*Spellbound.config.hearty.HEALTH_FACTOR_PER_LEVEL)+
                            (SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getAllSlots(),SBEnchantments.HEARTY,entity)*Spellbound.config.hearty.HEALTH_FACTOR_BASE)
                            ,AttributeModifier.Operation.MULTIPLY_TOTAL);
            SpellboundUtil.ReplaceAttributeModifier(att, mod);
            if(entity.getHealth() > entity.getMaxHealth()){
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }
}
