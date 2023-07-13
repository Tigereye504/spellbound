package net.tigereye.spellbound;

import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.tigereye.spellbound.enchantments.target.AnyWeaponEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.ArmorMaybeShieldEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.AxeEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.RangedWeaponEnchantmentTarget;

public class SpellboundEarlyRiser implements Runnable {

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        final String enchantmentTarget = remapper.mapClassName(
                "intermediary",
                "net.minecraft.class_1886"
        );

        registerEnchantmentTargets(enchantmentTarget);
    }

    public static void registerEnchantmentTargets(String enchantmentTarget){
        /*ANY_WEAPON = */
        registerEnchantmentTargets(enchantmentTarget, AnyWeaponEnchantmentTarget.NAME,
                "net.tigereye.spellbound.enchantments.target.AnyWeaponEnchantmentTarget");
        /*ARMOR_MAYBE_SHIELD = */
        registerEnchantmentTargets(enchantmentTarget, ArmorMaybeShieldEnchantmentTarget.NAME,
                "net.tigereye.spellbound.enchantments.target.ArmorMaybeShieldEnchantmentTarget");
        /*AXE = */
        registerEnchantmentTargets(enchantmentTarget, AxeEnchantmentTarget.NAME,
                "net.tigereye.spellbound.enchantments.target.AxeEnchantmentTarget");
        /*RANGED_WEAPON = */
        registerEnchantmentTargets(enchantmentTarget, RangedWeaponEnchantmentTarget.NAME,
                "net.tigereye.spellbound.enchantments.target.RangedWeaponEnchantmentTarget");
    }

    private static void registerEnchantmentTargets(String enchantmentTarget, String name, String path){
        ClassTinkerers.enumBuilder(enchantmentTarget).addEnumSubclass(
                name,path).build();
        //return ClassTinkerers.getEnum(EnchantmentTarget.class, name);
    }
}
