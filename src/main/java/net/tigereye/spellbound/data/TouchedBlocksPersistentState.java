package net.tigereye.spellbound.data;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.tigereye.spellbound.Spellbound;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class TouchedBlocksPersistentState extends PersistentState {

    public static final String TOUCHED_BLOCKS_LIST_KEY = Spellbound.MODID+"TouchedBlocks";
    private final Map<ChunkPos, Set<Long>> touchedBlocks = new HashMap<>();

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
            this.markDirty();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        Set<Map.Entry<ChunkPos, Set<Long>>> chunkSet = touchedBlocks.entrySet();
        for (Map.Entry<ChunkPos, Set<Long>> chunk : chunkSet) {
            NbtCompound nbtChunk = new NbtCompound();
            nbtChunk.putInt("x",chunk.getKey().x);
            nbtChunk.putInt("z",chunk.getKey().z);
            nbtChunk.putLongArray("blocks",chunk.getValue().stream().toList());
            nbtList.add(nbtChunk);
        }
        nbt.put("TouchedChunks",nbtList);
        return nbt;
    }
    public static TouchedBlocksPersistentState createFromNbt(NbtCompound nbt){
        TouchedBlocksPersistentState tbpState = new TouchedBlocksPersistentState();
        if(nbt.contains("TouchedChunks")){
            NbtList chunkList = nbt.getList("TouchedChunks", NbtElement.COMPOUND_TYPE);
            for (NbtElement element:chunkList) {
                NbtCompound chunkNbt = (NbtCompound)element;
                ChunkPos chunkPos = new ChunkPos(chunkNbt.getInt("x"),chunkNbt.getInt("z"));
                Set<Long> blockSet = new HashSet<>(Arrays.stream(ArrayUtils.toObject(chunkNbt.getLongArray("blocks"))).toList());
                tbpState.touchedBlocks.put(chunkPos,blockSet);
            }
        }
        return tbpState;
    }

    public static TouchedBlocksPersistentState getTouchedBlocksPersistentState(ServerWorld world){
        PersistentStateManager persistentStateManager = world.getPersistentStateManager();
        return persistentStateManager.getOrCreate(
                TouchedBlocksPersistentState::createFromNbt,
                TouchedBlocksPersistentState::new,
                TOUCHED_BLOCKS_LIST_KEY);
    }
}
