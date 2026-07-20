package net.tigereye.spellbound;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.datafixers.DSL;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.tigereye.spellbound.enchantments.target.AnyWeaponEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.ArmorMaybeShieldEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.AxeEnchantmentTarget;
import net.tigereye.spellbound.enchantments.target.RangedWeaponEnchantmentTarget;

public class SpellboundEarlyRiser implements Runnable {


    public static final DSL.TypeReference SAVED_DATA_RESURFACING_REF = () -> "saved_data/spellbound/resurfacing";
    public static final DSL.TypeReference SAVED_DATA_TOUCHED_BLOCKS_REF = () -> "saved_data/spellbound/touched_blocks";

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        final String enchantmentTarget = remapper.mapClassName(
                "intermediary",
                "net.minecraft.class_1886"
        );

        registerEnchantmentTargets(enchantmentTarget);
        final String dataFixTypesTarget = remapper.mapClassName(
                "intermediary",
                "net.minecraft.class_4284"
        );

        registerDataFixType(dataFixTypesTarget);
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

    public static void registerDataFixType(String dataFixTypesTarget) {
        registerDataFixType(dataFixTypesTarget, "SAVED_DATA_RESURFACING",SAVED_DATA_RESURFACING_REF);
        registerDataFixType(dataFixTypesTarget, "SAVED_DATA_TOUCHED_BLOCKS",SAVED_DATA_TOUCHED_BLOCKS_REF);
    }

    private static void registerDataFixType(String dataFixTypesTarget, String name, DSL.TypeReference ref){
        ClassTinkerers.enumBuilder(dataFixTypesTarget,DSL.TypeReference.class).addEnum(
                name,ref).build();
    }
}
