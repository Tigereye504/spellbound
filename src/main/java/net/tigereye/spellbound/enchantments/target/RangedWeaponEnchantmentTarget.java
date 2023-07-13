package net.tigereye.spellbound.enchantments.target;

import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mixins.EnchantmentTargetMixin;

public class RangedWeaponEnchantmentTarget extends EnchantmentTargetMixin {
    public static final String NAME = Spellbound.MODID+"_RANGED_WEAPON";
    @Override
    public boolean isAcceptableItem(Item item) {
        return item instanceof TridentItem
                || item instanceof RangedWeaponItem;
    }
}
