package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.blocks.CrateBlock;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import net.tigereye.spellbound.items.BagOfRocks;
import net.tigereye.spellbound.items.BagOfTrophies;

public class SBItems {

    public static final Item BAG_OF_ROCKS = new BagOfRocks(new Item.Properties());
    public static final Item BAG_OF_TROPHIES = new BagOfTrophies(new Item.Properties());
    public static final Item IRON_PEBBLE = new Item(new Item.Properties().stacksTo(64));
    public static final Item COPPER_PEBBLE = new Item(new Item.Properties().stacksTo(64));
    public static final Item GOLD_PEBBLE = new Item(new Item.Properties().stacksTo(64));
    public static final Item DIAMOND_SHARD = new Item(new Item.Properties().stacksTo(64));
    public static final Item EMERALD_SHARD = new Item(new Item.Properties().stacksTo(64));
    //public static final Item ANCIENT_SHARD = new Item(new Item.Settings().maxCount(64));
    public static final Block CRATE = new CrateBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL));

    public static final BlockEntityType<CrateBlockEntity> CRATE_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(Spellbound.MODID, "crate_block_entity"),
            BlockEntityType.Builder.of(CrateBlockEntity::new, CRATE).build(null)
    );
    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "bag_of_rocks"), BAG_OF_ROCKS);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "bag_of_trophies"), BAG_OF_TROPHIES);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "iron_pebble"), IRON_PEBBLE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "copper_pebble"), COPPER_PEBBLE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "gold_pebble"), GOLD_PEBBLE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "diamond_shard"), DIAMOND_SHARD);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "emerald_shard"), EMERALD_SHARD);
        //Registry.register(Registry.ITEM, new Identifier(Spellbound.MODID, "ancient_shard"), ANCIENT_SHARD);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(Spellbound.MODID, "crate"), CRATE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(Spellbound.MODID, "crate"), new BlockItem(CRATE, new Item.Properties().stacksTo(64)));

        registerItemGroups();
    }

    private static void registerItemGroups(){
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS).register(entries -> {
            entries.accept(BAG_OF_ROCKS);
            entries.accept(BAG_OF_TROPHIES);
            entries.accept(IRON_PEBBLE);
            entries.accept(COPPER_PEBBLE);
            entries.accept(GOLD_PEBBLE);
            entries.accept(DIAMOND_SHARD);
            entries.accept(EMERALD_SHARD);
        });
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.BUILDING_BLOCKS).register(entries -> entries.accept(CRATE));
    }
}
