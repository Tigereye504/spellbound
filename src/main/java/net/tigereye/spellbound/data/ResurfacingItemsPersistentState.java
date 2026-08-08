package net.tigereye.spellbound.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.tigereye.modifydropsapi.api.GenerateLootCallbackAddLoot;
import net.tigereye.spellbound.Spellbound;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResurfacingItemsPersistentState extends SavedData {

    public static final String RESURFACING_ITEMS_LIST_KEY = Spellbound.MODID+"_resurfacing_items";

    private final List<ItemStack> resurfacingQueue = new LinkedList<>();

    public static SavedData.Factory<ResurfacingItemsPersistentState> factory() {
      return new SavedData.Factory<ResurfacingItemsPersistentState>(ResurfacingItemsPersistentState::new, ResurfacingItemsPersistentState::load, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
    }

    public boolean canResurfaceItem(){
        return !resurfacingQueue.isEmpty();
    }

    public void PushItem(ItemStack stack){
        resurfacingQueue.add(stack);
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info("Pushed "+stack.getHoverName().getString()+". "+resurfacingQueue.size()+" items are waiting to resurface.");
        }
        setDirty();
    }

    public ItemStack PopItem(){
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(resurfacingQueue.size()+" items are waiting to resurface before pop.");
        }
        setDirty();
        return canResurfaceItem() ? resurfacingQueue.remove(0) : null;
    }

    public List<ItemStack> PopMultipleWithChance(int count, float chance, RandomSource random){
        List<ItemStack> output = new ArrayList<>();
        //check from back to front to avoid changing the position of items yet to be checked.
        for(int i = Math.min(resurfacingQueue.size(),count)-1; i >= 0; i--){
            if(resurfacingQueue.size() > i){
                if(random.nextFloat() < chance){
                   output.add(resurfacingQueue.remove(i));
                }
            }
        }
        setDirty();
        return output;
    }
    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag nbtList = new ListTag();
        for (ItemStack stack : resurfacingQueue) {
            nbtList.add(stack.save(provider));
        }
        compoundTag.put(RESURFACING_ITEMS_LIST_KEY,nbtList);
        return compoundTag;
    }
    public static ResurfacingItemsPersistentState load(CompoundTag compoundTag, HolderLookup.Provider provider){
        ResurfacingItemsPersistentState ripState = new ResurfacingItemsPersistentState();
        if(compoundTag.contains(RESURFACING_ITEMS_LIST_KEY)){
            ListTag chunkList = compoundTag.getList(RESURFACING_ITEMS_LIST_KEY, Tag.TAG_COMPOUND);
            for (Tag element:chunkList) {
                ItemStack.parse(provider,element).ifPresent((stack) -> ripState.resurfacingQueue.add(stack));
            }
        }
        return ripState;
    }

    public static ResurfacingItemsPersistentState getResurfacingItemsPersistentState(MinecraftServer server){
        DimensionDataStorage persistentStateManager = server
                .getLevel(Level.OVERWORLD).getDataStorage();
        return persistentStateManager.computeIfAbsent( factory(),
                RESURFACING_ITEMS_LIST_KEY);
    }

    public static void registerResurfacingInChest(){
        GenerateLootCallbackAddLoot.EVENT.register((type, lootContext) -> {
            List<ItemStack> loot = new ArrayList<>();
            if(type == LootContextParamSets.CHEST){
                Entity entity = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
                if(entity != null) {
                    MinecraftServer server = entity.getServer();
                    if (server != null) {
                        ResurfacingItemsPersistentState ripState = getResurfacingItemsPersistentState(server);
                        if (ripState.canResurfaceItem()) {
                            loot.addAll(ripState.PopMultipleWithChance(Spellbound.config.resurfacing.ATTEMPTS_PER_CHEST,
                                    Spellbound.config.resurfacing.CHANCE_PER_ATTEMPT,entity.level().getRandom()));
                        }
                    }
                }
            }
            return loot;
        });
    }
}
