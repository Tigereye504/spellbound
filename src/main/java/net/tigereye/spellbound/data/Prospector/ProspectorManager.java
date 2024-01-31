package net.tigereye.spellbound.data.Prospector;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.TouchedBlocksPersistentState;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class ProspectorManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "prospector";
    private final ProspectorSerializer SERIALIZER = new ProspectorSerializer();
    private static final Map<Identifier, Float> baseDropRateMap = new HashMap<>();
    private static final Map<Identifier, List<Pair<Identifier,Float>>> blockDropBonusMap = new HashMap<>();
    private static final Map<TagKey<Block>, List<Pair<Identifier,Float>>> tagDropBonusMap = new HashMap<>();
    private static final Map<DimensionType, TouchedBlocksPersistentState> tbpState = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Spellbound.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        baseDropRateMap.clear();
        blockDropBonusMap.clear();
        Spellbound.LOGGER.info("Loading Spellbound Prospector Treasures.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
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
                    List<Pair<Identifier,Float>> value;
                    Pair<Identifier,Float> pair = new Pair<>(prospectorData.treasure,prospectorData.frequency);
                    TagKey<Block> tag = TagKey.of(RegistryKeys.BLOCK,prospectorData.material);
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
                    List<Pair<Identifier,Float>> value;
                    Pair<Identifier,Float> pair = new Pair<>(prospectorData.treasure,prospectorData.frequency);
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

    public static Map<Identifier, Float> getBaseDropRateMap(){
        return baseDropRateMap;
    }

    public static Map<Identifier, Float> getDropRateMapWithBonuses(ServerWorld world, BlockPos center, int radius){
        Map<Identifier, Float> output = new HashMap<>(getBaseDropRateMap());
        Set<Block> foundBlocks = new HashSet<>();
        BlockPos lowerCorner = center.add(-radius,-radius,-radius);
        int size = (radius*2)+1;
        BlockPos target;
        BlockState targetBlock;
        for(int y = 0; y < size; y++){
            if(lowerCorner.getY()+y >= world.getBottomY() && lowerCorner.getY()+y <= world.getTopY()){
                for(int x = 0; x < size; x++){
                    for(int z = 0; z < size; z++){
                        target = lowerCorner.add(x,y,z);
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
            Identifier id = Registries.BLOCK.getId(block);
            //see if any tag bonuses are had
            Iterator<TagKey<Block>> iter = tagsLeft.iterator();
            while(iter.hasNext()){
                TagKey<Block> tag = iter.next();
                if(block.getRegistryEntry().isIn(tag)){
                    for (Pair<Identifier,Float> pair: tagDropBonusMap.get(tag)) {
                        output.put(pair.getLeft(),output.getOrDefault(pair.getLeft(),0f) + pair.getRight());
                    }
                    iter.remove();
                }
            }
            //see if any block bonuses are had
            if(blockDropBonusMap.containsKey(id)){
                for (Pair<Identifier,Float> pair: blockDropBonusMap.get(id)) {
                    output.put(pair.getLeft(),output.getOrDefault(pair.getLeft(),0f) + pair.getRight());
                }
            }
        }
        return output;
    }

    public static boolean detectTouchedBlock(ServerWorld world, BlockPos pos){
        if(Spellbound.config.prospector.DETECT_ABUSE) {
            DimensionType dimensionType = world.getDimension();
            if (!tbpState.containsKey(dimensionType)) {
                tbpState.put(dimensionType,TouchedBlocksPersistentState.getTouchedBlocksPersistentState(world));
            }
            return tbpState.get(dimensionType).isBlockTouched(pos);
        }
        return false;
    }
}
