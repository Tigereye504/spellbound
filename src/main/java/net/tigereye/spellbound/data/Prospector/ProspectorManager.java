package net.tigereye.spellbound.data.Prospector;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.dimension.DimensionType;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.TouchedBlocksPersistentState;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class ProspectorManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "prospector";
    private final ProspectorSerializer SERIALIZER = new ProspectorSerializer();
    private static final Map<ResourceLocation, Float> baseDropRateMap = new HashMap<>();
    private static final Map<ResourceLocation, List<Tuple<ResourceLocation,Float>>> blockDropBonusMap = new HashMap<>();
    private static final Map<TagKey<Block>, List<Tuple<ResourceLocation,Float>>> tagDropBonusMap = new HashMap<>();
    private static final Map<DimensionType, TouchedBlocksPersistentState> tbpState = new HashMap<>();

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Spellbound.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        baseDropRateMap.clear();
        blockDropBonusMap.clear();
        Spellbound.LOGGER.info("Loading Spellbound Prospector Treasures.");
        manager.listResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.open()) {
                Reader reader = new InputStreamReader(stream);
                ProspectorData prospectorData = SERIALIZER.read(id,new Gson().fromJson(reader, ProspectorJsonFormat.class));
                if(prospectorData.material == null){
                    //this is a universal drop rate
                    if(baseDropRateMap.containsKey(prospectorData.treasure)){
                        Spellbound.LOGGER.warn("Duplicate universal prospector entry " +prospectorData.treasure+ ".");
                    }
                    baseDropRateMap.put(prospectorData.treasure, prospectorData.frequency);
                }
                else if(prospectorData.materialIsTag) {
                    List<Tuple<ResourceLocation,Float>> value;
                    Tuple<ResourceLocation,Float> pair = new Tuple<>(prospectorData.treasure,prospectorData.frequency);
                    TagKey<Block> tag = TagKey.create(Registries.BLOCK,prospectorData.material);
                    if(!tagDropBonusMap.containsKey(tag)){
                        value = new LinkedList<>();
                        tagDropBonusMap.put(tag,value);
                    }
                    else{
                        value = tagDropBonusMap.get(tag);
                    }
                    value.add(pair);
                }
                else{
                    List<Tuple<ResourceLocation,Float>> value;
                    Tuple<ResourceLocation,Float> pair = new Tuple<>(prospectorData.treasure,prospectorData.frequency);
                    if(!blockDropBonusMap.containsKey(prospectorData.material)){
                        value = new LinkedList<>();
                        blockDropBonusMap.put(prospectorData.material,value);
                    }
                    else{
                        value = blockDropBonusMap.get(prospectorData.material);
                    }

                    value.add(pair);
                }
            } catch(Exception e) {
                Spellbound.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Spellbound.LOGGER.info("Loaded "+ baseDropRateMap.size()+" Prospector Universal Drops.");
        Spellbound.LOGGER.info("Loaded "+ tagDropBonusMap.size()+" Prospector Tags.");
        Spellbound.LOGGER.info("Loaded "+ blockDropBonusMap.size()+" Prospector Blocks.");
    }

    public static Map<ResourceLocation, Float> getBaseDropRateMap(){
        return baseDropRateMap;
    }

    public static Map<ResourceLocation, Float> getDropRateMapWithBonuses(ServerLevel world, BlockPos center, int radius){
        Map<ResourceLocation, Float> output = new HashMap<>(getBaseDropRateMap());
        Set<Block> foundBlocks = new HashSet<>();
        BlockPos lowerCorner = center.offset(-radius,-radius,-radius);
        int size = (radius*2)+1;
        BlockPos target;
        BlockState targetBlock;
        for(int y = 0; y < size; y++){
            if(lowerCorner.getY()+y >= world.getMinBuildHeight() && lowerCorner.getY()+y <= world.getMaxBuildHeight()){
                for(int x = 0; x < size; x++){
                    for(int z = 0; z < size; z++){
                        target = lowerCorner.offset(x,y,z);
                        targetBlock = world.getBlockState(target);
                        if(!foundBlocks.contains(targetBlock.getBlock())) {
                            if(!detectTouchedBlock(world,target)) { //this is kinda expensive, so it is checked last
                                foundBlocks.add(targetBlock.getBlock());
                            }
                        }
                    }
                }
            }
        }

        Set<TagKey<Block>> tagsLeft = new HashSet<>(tagDropBonusMap.keySet());
        for (Block block: foundBlocks) {
            //see if any tag bonuses are had
            Iterator<TagKey<Block>> iter = tagsLeft.iterator();
            while(iter.hasNext()){
                TagKey<Block> tag = iter.next();
                if(BuiltInRegistries.BLOCK.wrapAsHolder(block).is(tag)){
                    for (Tuple<ResourceLocation,Float> pair: tagDropBonusMap.get(tag)) {
                        output.put(pair.getA(),output.getOrDefault(pair.getA(),0f) + pair.getB());
                    }
                    iter.remove();
                }
            }
            //see if any block bonuses are had
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
            if(blockDropBonusMap.containsKey(id)){
                for (Tuple<ResourceLocation,Float> pair: blockDropBonusMap.get(id)) {
                    output.put(pair.getA(),output.getOrDefault(pair.getA(),0f) + pair.getB());
                }
            }
        }
        return output;
    }

    public static boolean detectTouchedBlock(ServerLevel world, BlockPos pos){
        if(Spellbound.config.prospector.DETECT_ABUSE) {
            DimensionType dimensionType = world.dimensionType();
            if (!tbpState.containsKey(dimensionType)) {
                tbpState.put(dimensionType,TouchedBlocksPersistentState.getTouchedBlocksPersistentState(world));
            }
            return tbpState.get(dimensionType).isBlockTouched(pos);
        }
        return false;
    }
}
