package net.tigereye.spellbound.data;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class ProspectorManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "prospector";
    private final ProspectorSerializer SERIALIZER = new ProspectorSerializer();
    private static Map<Identifier, Float> dropRateMap = new HashMap<>();
    private static Map<Identifier, List<Pair<Identifier,Float>>> oreBonusMap = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Spellbound.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        dropRateMap.clear();
        oreBonusMap.clear();
        Spellbound.LOGGER.info("Loading Spellbound Prospector Treasures.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                ProspectorData prospectorData = SERIALIZER.read(id,new Gson().fromJson(reader, ProspectorJsonFormat.class));
                if(prospectorData.bonusOre.isEmpty()){
                    //this is a universal drop rate
                    if(dropRateMap.containsKey(prospectorData.treasure)){
                        Spellbound.LOGGER.warn("Duplicate prospector entry " +prospectorData.treasure+ ".");
                    }
                    dropRateMap.put(prospectorData.treasure, prospectorData.frequency);
                }
                else{
                    for (Identifier ore: prospectorData.bonusOre) {
                        List<Pair<Identifier,Float>> value;
                        Pair<Identifier,Float> pair = new Pair<>(prospectorData.treasure,prospectorData.frequency);
                        if(!oreBonusMap.containsKey(ore)){
                            value = new LinkedList<>();
                            oreBonusMap.put(ore,value);
                        }
                        else{
                            value = oreBonusMap.get(ore);
                        }

                        value.add(pair);
                    }
                }
            } catch(Exception e) {
                Spellbound.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Spellbound.LOGGER.info("Loaded "+ dropRateMap.size()+" Prospector Treasures.");
        Spellbound.LOGGER.info("Loaded "+ oreBonusMap.size()+" Prospector Bonus Ores.");
    }

    public static Map<Identifier, Float> getDropRateMap(){
        return dropRateMap;
    }

    public static Map<Identifier, Float> getDropRateMapWithBonuses(World world, ItemStack tool, BlockPos center, int radius){
        Map<Identifier, Float> output = new HashMap<>(dropRateMap);
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
                        foundBlocks.add(targetBlock.getBlock());
                    }
                }
            }
        }
        for (Block block: foundBlocks) {
            Identifier id = Registry.BLOCK.getId(block);
            if(oreBonusMap.containsKey(id)){
                for (Pair<Identifier,Float> pair: oreBonusMap.get(id)) {
                    output.put(pair.getLeft(),output.getOrDefault(pair.getLeft(),0f) + pair.getRight());
                }
            }
        }
        return output;
    }
}
