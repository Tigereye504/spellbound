package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;

public class FleshWoundEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public FleshWoundEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
    }

    @Override
    public int getMinPower(int level) {
        return (level*11)-10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    @Override
    public int getMaxLevel() {

        if(Spellbound.config.FLESH_WOUND_ENABLED) return 4;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onRedHealthDamage(int level, ItemStack itemStack, LivingEntity entity, float amount) {
        if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(itemStack)) != itemStack){
            return;
        }
        if(itemStack.getItem() == Items.LEATHER_BOOTS ||
                itemStack.getItem() == Items.LEATHER_CHESTPLATE ||
                itemStack.getItem() == Items.LEATHER_LEGGINGS ||
                itemStack.getItem() == Items.LEATHER_HELMET){
            if(((DyeableArmorItem) itemStack.getItem()).getColor(itemStack) == 1908001) {
                level *= 3;
            }
        }
        entity.setAbsorptionAmount(entity.getAbsorptionAmount()+(level*amount*Spellbound.config.FLESH_WOUND_ABSORPTION_PER_DAMAGE_PER_LEVEL));
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return !(other instanceof ProtectionEnchantment) && super.canAccept(other);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }

    private static void ReplaceAttributeModifier(EntityAttributeInstance att, EntityAttributeModifier mod)
    {
        //removes any existing mod and replaces it with the updated one.
        att.removeModifier(mod);
        att.addPersistentModifier(mod);
    }
}
