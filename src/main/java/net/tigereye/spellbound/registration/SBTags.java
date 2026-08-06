package net.tigereye.spellbound.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.tigereye.spellbound.Spellbound;

import java.util.*;


public class SBTags {

    public static final List<TagKey<Enchantment>> ENCHANTMENT_CATEGORIES = new LinkedList<>();
    public static final Map<TagKey<Enchantment>,List<Enchantment>> CATEGORY_PARENTS = new HashMap<>();
    public static final TagKey<Enchantment> DAMAGE_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"damage"));
    public static final TagKey<Enchantment> EFFICIENCY_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"efficiency"));
    public static final TagKey<Enchantment> FORTUNE_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"fortune"));
    public static final TagKey<Enchantment> LOOTING_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"looting"));
    public static final TagKey<Enchantment> LURE_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"lure"));
    public static final TagKey<Enchantment> META_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"meta"));
    public static final TagKey<Enchantment> PROTECTION_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"protection"));
    public static final TagKey<Enchantment> REPAIR_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"repair"));
    public static final TagKey<Enchantment> RETALIATION_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"retaliation"));
    public static final TagKey<Enchantment> UNBREAKING_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"unbreaking"));
    public static final TagKey<Enchantment> UTILITY_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, new ResourceLocation(Spellbound.MODID,"utility"));

    public static final TagKey<Item> ALL_WEAPONS_ENCHANTABLE = TagKey.create(Registries.ITEM, new ResourceLocation(Spellbound.MODID,"enchantable/all_weapons"));
    public static final TagKey<Item> ARMOR_AND_SHIELD_ENCHANTABLE = TagKey.create(Registries.ITEM, new ResourceLocation(Spellbound.MODID,"enchantable/armor_and_shield"));
    public static final TagKey<Item> RANGED_WEAPONS_ENCHANTABLE = TagKey.create(Registries.ITEM, new ResourceLocation(Spellbound.MODID,"enchantable/ranged_weapons"));
    public static final TagKey<Item> JOUSTING_ENCHANTABLE = TagKey.create(Registries.ITEM, new ResourceLocation(Spellbound.MODID,"enchantable/jousting"));
    public static final TagKey<Item> AXE_ENCHANTABLE = TagKey.create(Registries.ITEM, new ResourceLocation(Spellbound.MODID,"enchantable/axes"));

    public static void register(){
        ENCHANTMENT_CATEGORIES.add(DAMAGE_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(EFFICIENCY_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(FORTUNE_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(LURE_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(LOOTING_ENCHANTMENTS);
        ENCHANTMENT_CATEGORIES.add(META_ENCHANTMENTS);
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
        parentList.add(Enchantments.FORTUNE);
        parentList.add(Enchantments.SILK_TOUCH);
        CATEGORY_PARENTS.put(FORTUNE_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.LURE);
        CATEGORY_PARENTS.put(LURE_ENCHANTMENTS, parentList);
        parentList = new ArrayList<>();
        parentList.add(Enchantments.LOOTING);
        CATEGORY_PARENTS.put(LOOTING_ENCHANTMENTS, parentList);
        //meta enchantments have no parents
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
        parentList.add(Enchantments.PUNCH);
        parentList.add(Enchantments.RIPTIDE);
        parentList.add(Enchantments.CHANNELING);
        parentList.add(Enchantments.FROST_WALKER);
        parentList.add(Enchantments.SOUL_SPEED);
        parentList.add(Enchantments.AQUA_AFFINITY);
        CATEGORY_PARENTS.put(UTILITY_ENCHANTMENTS, parentList);
    }
}
