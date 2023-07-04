package net.tigereye.spellbound.data;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.tigereye.modifydropsapi.api.GenerateLootCallbackAddLoot;
import net.tigereye.spellbound.Spellbound;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ResurfacingItemsPersistentState extends PersistentState {

    public static final String RESURFACING_ITEMS_LIST_KEY = Spellbound.MODID+"ResurfacingItems";

    private final List<ItemStack> resurfacingQueue = new LinkedList<>();

    public boolean canResurfaceItem(){
        return !resurfacingQueue.isEmpty();
    }

    public void PushItem(ItemStack stack){
        resurfacingQueue.add(stack);
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info("Pushed "+stack.getName().getString()+". "+resurfacingQueue.size()+" items are waiting to resurface.");
        }
        markDirty();
    }

    public ItemStack PopItem(){
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(resurfacingQueue.size()+" items are waiting to resurface before pop.");
        }
        markDirty();
        return canResurfaceItem() ? resurfacingQueue.remove(0) : null;
    }

    public List<ItemStack> PopMultipleWithChance(int count, float chance, Random random){
        List<ItemStack> output = new ArrayList<>();
        //check from back to front to avoid changing the position of items yet to be checked.
        for(int i = Math.min(resurfacingQueue.size(),count)-1; i >= 0; i--){
            if(resurfacingQueue.size() > i){
                if(random.nextFloat() < chance){
                   output.add(resurfacingQueue.remove(i));
                }
            }
        }
        markDirty();
        return output;
    }
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        for (ItemStack stack : resurfacingQueue) {
            nbtList.add(stack.writeNbt(new NbtCompound()));
        }
        nbt.put(RESURFACING_ITEMS_LIST_KEY,nbtList);
        return nbt;
    }
    public static ResurfacingItemsPersistentState createFromNbt(NbtCompound nbt){
        ResurfacingItemsPersistentState ripState = new ResurfacingItemsPersistentState();
        if(nbt.contains(RESURFACING_ITEMS_LIST_KEY)){
            NbtList chunkList = nbt.getList(RESURFACING_ITEMS_LIST_KEY, NbtElement.COMPOUND_TYPE);
            for (NbtElement element:chunkList) {
                ripState.resurfacingQueue.add(ItemStack.fromNbt((NbtCompound)element));
            }
        }
        return ripState;
    }

    public static ResurfacingItemsPersistentState getResurfacingItemsPersistentState(MinecraftServer server){
        PersistentStateManager persistentStateManager = server
                .getWorld(World.OVERWORLD).getPersistentStateManager();
        return persistentStateManager.getOrCreate(
                ResurfacingItemsPersistentState::createFromNbt,
                ResurfacingItemsPersistentState::new,
                RESURFACING_ITEMS_LIST_KEY);
    }

    public static void registerResurfacingInChest(){
        GenerateLootCallbackAddLoot.EVENT.register((type, lootContext) -> {
            List<ItemStack> loot = new ArrayList<>();
            if(type == LootContextTypes.CHEST){
                Entity entity = lootContext.get(LootContextParameters.THIS_ENTITY);
                if(entity != null) {
                    MinecraftServer server = entity.getServer();
                    if (server != null) {
                        ResurfacingItemsPersistentState ripState = getResurfacingItemsPersistentState(server);
                        if (ripState.canResurfaceItem()) {
                            loot.addAll(ripState.PopMultipleWithChance(Spellbound.config.resurfacing.ATTEMPTS_PER_CHEST,
                                    Spellbound.config.resurfacing.CHANCE_PER_ATTEMPT,entity.world.getRandom()));
                        }
                    }
                }
            }
            return loot;
        });
    }
}
