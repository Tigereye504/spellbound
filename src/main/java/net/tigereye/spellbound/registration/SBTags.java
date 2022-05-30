package net.tigereye.spellbound.registration;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.Spellbound;

import java.util.*;


public class SBTags {

    public static final List<TagKey<Enchantment>> ENCHANTMENT_CATEGORIES = new LinkedList<>();
    public static final Map<TagKey<Enchantment>,List<Enchantment>> CATEGORY_PARENTS = new HashMap<>();
    public static final TagKey<Enchantment> DAMAGE_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"damage"));
    public static final TagKey<Enchantment> EFFICIENCY_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"efficiency"));
    public static final TagKey<Enchantment> LURE_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"lure"));
    public static final TagKey<Enchantment> PERSONALITY_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"personality"));
    public static final TagKey<Enchantment> PROTECTION_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"protection"));
    public static final TagKey<Enchantment> REPAIR_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"repair"));
    public static final TagKey<Enchantment> RETALIATION_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"retaliation"));
    public static final TagKey<Enchantment> UNBREAKING_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"unbreaking"));
    public static final TagKey<Enchantment> UTILITY_ENCHANTMENTS = TagKey.of(Registry.ENCHANTMENT_KEY, new Identifier(Spellbound.MODID,"utility"));

    public static void register(){
        ENCHANTMENT_CATEGORIES.add(DAMAGE_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(EFFICIENCY_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(LURE_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(PERSONALITY_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(PROTECTION_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(REPAIR_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(RETALIATION_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(UNBREAKING_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(UTILITY_ENCHANTMENTS);
        List<Enchantment> parentList = new ArrayList<>();
        parentList.add(Enchantments.SHARPNESS);
        parentList.add(Enchantments.POWER);
        CATEGORY_PARENTS.put(DAMAGE_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.EFFICIENCY);
        CATEGORY_PARENTS.put(EFFICIENCY_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.LURE);
        CATEGORY_PARENTS.put(LURE_ENCHANTMENTS, parentList);
        //personality enchantments have no parents
        parentList = new ArrayList<>();
        parentList.add(Enchantments.PROTECTION);
        CATEGORY_PARENTS.put(PROTECTION_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.MENDING);
        CATEGORY_PARENTS.put(REPAIR_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.THORNS);
        CATEGORY_PARENTS.put(RETALIATION_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.UNBREAKING);
        CATEGORY_PARENTS.put(UNBREAKING_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.KNOCKBACK);
        parentList.add(Enchantments.FROST_WALKER);
        parentList.add(Enchantments.SOUL_SPEED);
        parentList.add(Enchantments.AQUA_AFFINITY);
        CATEGORY_PARENTS.put(UTILITY_ENCHANTMENTS, parentList);
    }
}
