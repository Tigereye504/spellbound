package net.tigereye.spellbound.data;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.tigereye.spellbound.Spellbound;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class TouchedBlocksPersistentState extends SavedData {

    public static final String TOUCHED_BLOCKS_LIST_KEY = Spellbound.MODID+"_touched_blocks";
    private final Map<ChunkPos, Set<Long>> touchedBlocks = new HashMap<>();

    public static SavedData.Factory<TouchedBlocksPersistentState> factory() {
      return new SavedData.Factory<TouchedBlocksPersistentState>(TouchedBlocksPersistentState::new, TouchedBlocksPersistentState::load, DataFixTypes.SAVED_DATA_FORCED_CHUNKS);
    }

    public boolean isBlockTouched(BlockPos pos){
        Set<Long> chunkSet = touchedBlocks.get(new ChunkPos(pos.getX() >> 4,pos.getZ() >> 4));
        boolean touched = chunkSet != null && chunkSet.contains(pos.asLong());
        if(Spellbound.DEBUG){
            if(touched){
                Spellbound.LOGGER.info("Block "+pos.getX()+" "+pos.getY()+" "+pos.getZ()+" has been touched.");
            }
            else{
                Spellbound.LOGGER.info("Block "+pos.getX()+" "+pos.getY()+" "+pos.getZ()+" is untouched.");
            }
        }
        return touched;
    }

    public void TouchBlock(BlockPos pos){
        ChunkPos chunkPos = new ChunkPos(pos.getX() >> 4,pos.getZ() >> 4);
        Set<Long> blockSet = touchedBlocks.getOrDefault(chunkPos,new HashSet<>());
        if (!blockSet.contains(pos.asLong())) {
            if(Spellbound.DEBUG) {
                Spellbound.LOGGER.info("Touching Block " + pos.getX() + " " + pos.getY() + " " + pos.getZ() + ".");
            }
            blockSet.add(pos.asLong());
            touchedBlocks.put(chunkPos, blockSet);
            this.setDirty();
        }
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        ListTag nbtList = new ListTag();
        Set<Map.Entry<ChunkPos, Set<Long>>> chunkSet = touchedBlocks.entrySet();
        for (Map.Entry<ChunkPos, Set<Long>> chunk : chunkSet) {
            CompoundTag nbtChunk = new CompoundTag();
            nbtChunk.putInt("x",chunk.getKey().x);
            nbtChunk.putInt("z",chunk.getKey().z);
            nbtChunk.putLongArray("blocks",chunk.getValue().stream().toList());
            nbtList.add(nbtChunk);
        }
        compoundTag.put("TouchedChunks",nbtList);
        return compoundTag;
    }
    public static TouchedBlocksPersistentState load(CompoundTag compoundTag, HolderLookup.Provider provider){
        TouchedBlocksPersistentState tbpState = new TouchedBlocksPersistentState();
        if(compoundTag.contains("TouchedChunks")){
            ListTag chunkList = compoundTag.getList("TouchedChunks", Tag.TAG_COMPOUND);
            for (Tag element:chunkList) {
                CompoundTag chunkNbt = (CompoundTag)element;
                ChunkPos chunkPos = new ChunkPos(chunkNbt.getInt("x"),chunkNbt.getInt("z"));
                Set<Long> blockSet = new HashSet<>(Arrays.stream(ArrayUtils.toObject(chunkNbt.getLongArray("blocks"))).toList());
                tbpState.touchedBlocks.put(chunkPos,blockSet);
            }
        }
        return tbpState;
    }

    public static TouchedBlocksPersistentState getTouchedBlocksPersistentState(ServerLevel world){
        DimensionDataStorage persistentStateManager = world.getDataStorage();
        return persistentStateManager.computeIfAbsent(factory(),TOUCHED_BLOCKS_LIST_KEY);
    }
}
