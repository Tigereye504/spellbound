package net.tigereye.spellbound.data.Chilled;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ChilledManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "chilled";
    private final ChilledSerializer SERIALIZER = new ChilledSerializer();
    private static final Map<ResourceLocation, ResourceLocation> recipeMap = new HashMap<>();

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Spellbound.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        recipeMap.clear();
        Spellbound.LOGGER.info("Loading Spellbound Chilled Recipes.");
        manager.listResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.open()) {
                Reader reader = new InputStreamReader(stream);
                ChilledData chilledData = SERIALIZER.read(id,new Gson().fromJson(reader, ChilledJsonFormat.class));
                if(recipeMap.containsKey(chilledData.block)){
                    Spellbound.LOGGER.warn("Chilled recipe "+chilledData.block.toString()+" -> "+recipeMap.get(chilledData.block).toString()+" overwritten with -> "+chilledData.result.toString()+".");
                }
                recipeMap.put(chilledData.block,chilledData.result);
            } catch(Exception e) {
                Spellbound.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Spellbound.LOGGER.info("Loaded "+ recipeMap.size()+" Chilled Recipes.");
    }

    public static Map<ResourceLocation, ResourceLocation> getRecipeMap(){
        return recipeMap;
    }

    @Nullable
    public static ResourceLocation getResult(BlockState state){
        return recipeMap.get(BuiltInRegistries.BLOCK.getKey(state.getBlock()));
    }
}
