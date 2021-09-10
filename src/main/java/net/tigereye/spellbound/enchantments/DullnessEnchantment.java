package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.*;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;

public class DullnessEnchantment extends SBEnchantment {

    public DullnessEnchantment() {
        super(Enchantment.Rarity.UNCOMMON, EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return 1 + 8 * (level - 1);
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        if(Spellbound.config.DULLNESS_ENABLED) return 5;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) || EnchantmentTarget.BREAKABLE.isAcceptableItem(stack.getItem());
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return -1.5f - level;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return !(other instanceof DamageEnchantment)
                && other.canCombine(Enchantments.SHARPNESS);
    }
}
