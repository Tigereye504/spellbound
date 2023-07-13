package net.tigereye.spellbound.enchantments.target;

import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mixins.EnchantmentTargetMixin;

public class AxeEnchantmentTarget extends EnchantmentTargetMixin {
    public static final String NAME = Spellbound.MODID+"_AXE";
    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof AxeItem;
    }
}
