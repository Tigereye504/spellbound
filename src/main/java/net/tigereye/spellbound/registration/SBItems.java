package net.tigereye.spellbound.registration;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.items.BagOfRocks;
import net.tigereye.spellbound.items.BagOfTrophies;

public class SBItems {

    public static final Item BAG_OF_ROCKS = new BagOfRocks(new Item.Settings().group(ItemGroup.MISC));
    public static final Item BAG_OF_TROPHIES = new BagOfTrophies(new Item.Settings().group(ItemGroup.MISC));
    public static final Item IRON_PEBBLE = new Item(new Item.Settings().maxCount(64).group(ItemGroup.MISC));
    public static final Item COPPER_PEBBLE = new Item(new Item.Settings().maxCount(64).group(ItemGroup.MISC));
    public static final Item GOLD_PEBBLE = new Item(new Item.Settings().maxCount(64).group(ItemGroup.MISC));
    public static final Item DIAMOND_SHARD = new Item(new Item.Settings().maxCount(64).group(ItemGroup.MISC));
    public static final Item EMERALD_SHARD = new Item(new Item.Settings().maxCount(64).group(ItemGroup.MISC));
    //public static final Item ANCIENT_SHARD = new Item(new Item.Settings().maxCount(64).group(ItemGroup.MISC));

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "bag_of_rocks"), BAG_OF_ROCKS);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "bag_of_trophies"), BAG_OF_TROPHIES);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "iron_pebble"), IRON_PEBBLE);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "copper_pebble"), COPPER_PEBBLE);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "gold_pebble"), GOLD_PEBBLE);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "diamond_shard"), DIAMOND_SHARD);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "emerald_shard"), EMERALD_SHARD);
        //Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "ancient_shard"), ANCIENT_SHARD);
    }
}
