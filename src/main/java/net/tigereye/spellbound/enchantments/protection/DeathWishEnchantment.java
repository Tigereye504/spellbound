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

public class DeathWishEnchantment extends SBEnchantment{

    private static final UUID DEATH_WISH_ID = UUID.fromString("d25ab5b2-455f-4825-9424-776e3f054b41");

    public DeathWishEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.deathWish.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                //new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.deathWish.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.deathWish.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.deathWish.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.deathWish.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.deathWish.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.deathWish.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.deathWish.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.deathWish.IS_FOR_SALE;}

    @Override
    public void onEquipmentChange(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if(att != null) {
            EntityAttributeModifier mod = new EntityAttributeModifier(DEATH_WISH_ID, "SpellboundDeathWishDamage",
                    (SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(entity.getItemsEquipped(),SBEnchantments.DEATH_WISH,entity)*Spellbound.config.deathWish.DAMAGE_FACTOR_PER_LEVEL)+
                            (SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getItemsEquipped(),SBEnchantments.DEATH_WISH,entity)*Spellbound.config.deathWish.DAMAGE_FACTOR_BASE)
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
