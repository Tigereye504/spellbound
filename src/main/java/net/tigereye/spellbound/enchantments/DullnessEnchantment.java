package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.*;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DullnessEnchantment extends SBEnchantment {

    public DullnessEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 1 + 8 * (level - 1);
    }

    public int getMaxPower(int level) {
        return super.getMinPower(level) + 20;
    }

    public int getMaxLevel() {
        return 5;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) || EnchantmentTarget.BREAKABLE.isAcceptableItem(stack.getItem());
    }

    public float getAttackDamage(int level, EntityGroup group) {
        return -1.5f - level;
    }

    public boolean canAccept(Enchantment other) {
        return !(other instanceof DamageEnchantment)
                && other.canCombine(Enchantments.SHARPNESS);
    }
}
