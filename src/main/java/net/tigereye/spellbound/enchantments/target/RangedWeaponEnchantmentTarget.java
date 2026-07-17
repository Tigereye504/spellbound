package net.tigereye.spellbound.enchantments.target;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TridentItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mixins.EnchantmentTargetMixin;

public class RangedWeaponEnchantmentTarget extends EnchantmentTargetMixin {
    public static final String NAME = Spellbound.MODID+"_RANGED_WEAPON";
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof TridentItem
                || item instanceof ProjectileWeaponItem;
    }
}
