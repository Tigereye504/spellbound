package net.tigereye.spellbound.enchantments;

import net.minecraft.item.ItemStack;

public interface CustomConditionsEnchantment {
    boolean isAcceptableAtTable(ItemStack stack);
}
