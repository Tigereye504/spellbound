package net.tigereye.spellbound.enchantments.target;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ShieldItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mixins.EnchantmentTargetMixin;

public class ArmorMaybeShieldEnchantmentTarget extends EnchantmentTargetMixin {
    public static final String NAME = Spellbound.MODID+"_ARMOR_MAYBE_SHIELD";
    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof ArmorItem
                || (Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS && item instanceof ShieldItem);
    }
}
