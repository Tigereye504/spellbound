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

    public static void register() {
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "bag_of_rocks"), BAG_OF_ROCKS);
        Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "bag_of_trophies"), BAG_OF_TROPHIES);
    }
}
