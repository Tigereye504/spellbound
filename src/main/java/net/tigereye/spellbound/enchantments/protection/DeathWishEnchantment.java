package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.UUID;

public class DeathWishEnchantment extends SBEnchantment{

    private static final UUID DEATH_WISH_ID = UUID.fromString("d25ab5b2-455f-4825-9424-776e3f054b41");

    public DeathWishEnchantment() {
        super(definition(Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS ? SBTags.ARMOR_AND_SHIELD_ENCHANTABLE : ItemTags.ARMOR_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.deathWish.RARITY), //enchantment weight
            Spellbound.config.deathWish.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.deathWish.BASE_POWER,Spellbound.config.deathWish.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.deathWish.BASE_POWER+Spellbound.config.deathWish.POWER_RANGE,Spellbound.config.deathWish.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.deathWish.RARITY-1), //level cost at anvil
            Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.deathWish.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.deathWish.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.deathWish.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.deathWish.IS_FOR_SALE;}

    @Override
    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        AttributeInstance att = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if(att != null) {
            AttributeModifier mod = new AttributeModifier(DEATH_WISH_ID, "SpellboundDeathWishDamage",
                    (SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(entity.getAllSlots(),SBEnchantments.DEATH_WISH,entity)*Spellbound.config.deathWish.DAMAGE_FACTOR_PER_LEVEL)+
                            (SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getAllSlots(),SBEnchantments.DEATH_WISH,entity)*Spellbound.config.deathWish.DAMAGE_FACTOR_BASE)
                            ,AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
            SpellboundUtil.ReplaceAttributeModifier(att, mod);
            if(entity.getHealth() > entity.getMaxHealth()){
                entity.setHealth(entity.getMaxHealth());
            }
        }
    }
}
