package net.tigereye.spellbound.data.SunkenTreasure;

import com.google.common.util.concurrent.AtomicDouble;
import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.storage.loot.LootTable;
import net.tigereye.spellbound.Spellbound;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class SunkenTreasureManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "sunken_treasure";
    private final SunkenTreasureSerializer SERIALIZER = new SunkenTreasureSerializer();
    private static final Map<ResourceKey<LootTable>, SunkenTreasureData> sunkenTreasureDataMap = new HashMap<>();

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Spellbound.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        sunkenTreasureDataMap.clear();
        Spellbound.LOGGER.info("Loading Spellbound Sunken Treasures.");
        manager.listResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.open()) {
                Reader reader = new InputStreamReader(stream);
                Map<ResourceKey<LootTable>,SunkenTreasureData> treasureMap = SERIALIZER.read(id,new Gson().fromJson(reader, SunkenTreasureJsonFormat.class));
                treasureMap.forEach((treasureId,treasureData) ->{
                    if(!sunkenTreasureDataMap.containsKey(treasureId) || treasureData.replace){
                        sunkenTreasureDataMap.put(treasureId,treasureData);
                    }
                    else{
                        mergeDimensionLists(sunkenTreasureDataMap.get(treasureId),treasureData);
                    }
                });
            } catch(Exception e) {
                Spellbound.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Spellbound.LOGGER.info("Loaded "+ sunkenTreasureDataMap.size()+" Sunken Treasures.");
    }

    private void mergeDimensionLists(SunkenTreasureData original, SunkenTreasureData donor) {
        if(original.isWhiteList == donor.isWhiteList){
            original.dimensionList.addAll(donor.dimensionList);
        }
        else{
            Set<ResourceLocation> whiteList = original.isWhiteList ? original.dimensionList : donor.dimensionList;
            Set<ResourceLocation> blackList = original.isWhiteList ? donor.dimensionList : original.dimensionList;
            whiteList.removeAll(blackList);
            original.dimensionList = whiteList;
            original.isWhiteList = true;
        }
    }

    public static ResourceKey<LootTable> getWeightedRandomLootTableId(int quality, ResourceLocation dimension, RandomSource random){
        AtomicDouble totalWeight = new AtomicDouble();
        List<Tuple<ResourceKey<LootTable> ,Double>> matchingLootTables = new LinkedList<>();
        //first, determine which loot tables can be rolled
        sunkenTreasureDataMap.forEach((treasureId,treasureData) -> {
            if(treasureData.isWhiteList == treasureData.dimensionList.contains(dimension) //if the list is white and contains the dimension, or it is black and does not, the dimension is a match
                    && treasureData.quality == quality){ //if the quality also matches, we add it to the roll table
                matchingLootTables.add(new Tuple<>(treasureId,treasureData.weight));
                totalWeight.addAndGet(treasureData.weight);
            }
        });
        //then, determine which one to return
        if(!matchingLootTables.isEmpty()) {
            double roll = random.nextDouble()*totalWeight.get();
            for (Tuple<ResourceKey<LootTable> , Double> pair : matchingLootTables) {
                if (pair.getB() > roll) {
                    return pair.getA();
                } else {
                    roll -= pair.getB();
                }
            }
            Spellbound.LOGGER.error("End of Sunken Treasure weighted list reached! This shouldn't happen!");
        }
        return null;
    }

    public static int getWeightedRandomQuality(RandomSource random, float luck){
        double totalWeight = 0;
        int i = 0;
        List<Double> modifiedWeights = new LinkedList<>();
        for (Double weight : Spellbound.config.sunkenTreasure.QUALITY_WEIGHTS){
            double modifiedWeight = (weight*Math.pow(1+(Spellbound.config.sunkenTreasure.LUCK_IMPACT_ON_QUALITY*luck),i));
            modifiedWeights.add(modifiedWeight);
            totalWeight += modifiedWeight;
            ++i;
        }
        double roll = random.nextDouble()*totalWeight;
        i = 0;
        for (double modifiedWeight : modifiedWeights){
            if(modifiedWeight > roll){
                return i;
            }
            else{
                roll -= modifiedWeight;
            }
            ++i;
        }
        Spellbound.LOGGER.error("End of Sunken Treasure quality weight reached! This shouldn't happen!");
        return 0;
    }

    //TODO: experiment with Sunken Treasure on tools and weapons

    /*public static void registerSunkenTreasure(){
        GenerateBlockLootCallbackModifyLoot.EVENT.register((type, lootContext, loot) -> {
            if(lootContext.get(LootContextParameters.BLOCK_STATE).getBlock() == SBItems.CRATE) {
                ItemStack tool = lootContext.get(LootContextParameters.TOOL);
                if (tool == null || EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, tool) <= 0) {
                    Identifier lootTableId = getWeightedRandomLootTableId(
                            getWeightedRandomQuality(lootContext.getRandom(), lootContext.getLuck()),
                            lootContext.getWorld().getDimensionKey().getValue(), ///.getDimensionKey().getValue(),
                            lootContext.getRandom());

                    LootContextParameterSet.Builder LPSBuilder = new LootContextParameterSet.Builder(lootContext.getWorld())
                            .add(LootContextParameters.ORIGIN,
                                    lootContext.get(LootContextParameters.ORIGIN))
                            .luck(lootContext.getLuck());
                    LootTable lootTable = lootContext.getWorld().getServer().getLootManager().getLootTable(lootTableId);
                    List<ItemStack> list = lootTable.generateLoot(LPSBuilder.build(LootContextTypes.CHEST));
                    loot.addAll(list);
                }
            }
            return loot;
        });
    }*/
}
