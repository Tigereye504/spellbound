package net.tigereye.spellbound.enchantments.target;

import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mixins.EnchantmentTargetMixin;

public class AnyWeaponEnchantmentTarget extends EnchantmentTargetMixin {
    public static final String NAME = Spellbound.MODID+"_ANY_WEAPON";
    @Override
    public boolean canEnchant(Item item) {
        return item instanceof TridentItem
                || item instanceof SwordItem
                || item instanceof AxeItem
                || item instanceof ProjectileWeaponItem;
    }
}
