package net.tigereye.spellbound.registration;

import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.enchantments.target.AnyWeaponEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.ArmorMaybeShieldEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.AxeEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.RangedWeaponEnchantmentTarget;

public class SBEnchantmentTargets {
    public static EnchantmentCategory ANY_WEAPON = ClassTinkerers.getEnum(EnchantmentCategory.class, AnyWeaponEnchantmentTarget.NAME);
    public static EnchantmentCategory ARMOR_MAYBE_SHIELD = ClassTinkerers.getEnum(EnchantmentCategory.class, ArmorMaybeShieldEnchantmentTarget.NAME);
    public static EnchantmentCategory AXE = ClassTinkerers.getEnum(EnchantmentCategory.class, AxeEnchantmentTarget.NAME);
    public static EnchantmentCategory RANGED_WEAPON = ClassTinkerers.getEnum(EnchantmentCategory.class, RangedWeaponEnchantmentTarget.NAME);
}
