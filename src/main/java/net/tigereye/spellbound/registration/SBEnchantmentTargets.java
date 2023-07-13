package net.tigereye.spellbound.registration;

import com.chocohead.mm.api.ClassTinkerers;
import net.minecraft.enchantment.EnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.AnyWeaponEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.ArmorMaybeShieldEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.AxeEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.RangedWeaponEnchantmentTarget;

public class SBEnchantmentTargets {
    public static EnchantmentTarget ANY_WEAPON = ClassTinkerers.getEnum(EnchantmentTarget.class, AnyWeaponEnchantmentTarget.NAME);
    public static EnchantmentTarget ARMOR_MAYBE_SHIELD = ClassTinkerers.getEnum(EnchantmentTarget.class, ArmorMaybeShieldEnchantmentTarget.NAME);
    public static EnchantmentTarget AXE = ClassTinkerers.getEnum(EnchantmentTarget.class, AxeEnchantmentTarget.NAME);
    public static EnchantmentTarget RANGED_WEAPON = ClassTinkerers.getEnum(EnchantmentTarget.class, RangedWeaponEnchantmentTarget.NAME);
}
