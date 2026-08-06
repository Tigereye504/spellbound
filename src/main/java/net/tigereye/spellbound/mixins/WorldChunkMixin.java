package net.tigereye.spellbound.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.UpgradeData;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.blending.BlendingData;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.ticks.TickContainerAccess;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.TouchedBlocksPersistentState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelChunk.class)
public class WorldChunkMixin extends ChunkAccess {

    @Final
    @Shadow
    Level level;
    public WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, LevelHeightAccessor heightLimitView, Registry<Biome> biome, long inhabitedTime, @Nullable LevelChunkSection[] sectionArrayInitializer, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
    }


    @Inject(at = @At(value="HEAD"), method = "setBlockState")
    public void spellboundSetBlockStateMixin(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir){
        if(this.level != null && Spellbound.config.prospector.DETECT_ABUSE){
            if(level instanceof ServerLevel serverWorld && getInhabitedTime() > Spellbound.config.prospector.NEW_CHUNK_GRACE_PERIOD) {
                TouchedBlocksPersistentState tbpState = TouchedBlocksPersistentState.getTouchedBlocksPersistentState(serverWorld);
                tbpState.TouchBlock(pos);
            }
        }
    }

    @Nullable
    @Shadow
    public BlockState setBlockState(BlockPos pos, BlockState state, boolean moved) {
        return null;
    }
    @Shadow
    public void setBlockEntity(BlockEntity blockEntity) {

    }
    @Shadow
    public void addEntity(Entity entity) {

    }
    @Shadow
    public ChunkStatus getStatus() {
        return null;
    }
    @Shadow
    public void removeBlockEntity(BlockPos pos) {

    }
    @Shadow
    @Nullable
    public CompoundTag getBlockEntityNbtForSaving(BlockPos pos) {
        return null;
    }
    @Shadow
    public TickContainerAccess<Block> getBlockTicks() {
        return null;
    }
    @Shadow
    public TickContainerAccess<Fluid> getFluidTicks() {
        return null;
    }
    @Shadow
    public TicksToSave getTicksForSerialization() {
        return null;
    }
    @Shadow
    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }
    @Shadow
    public BlockState getBlockState(BlockPos pos) {
        return null;
    }
    @Shadow
    public FluidState getFluidState(BlockPos pos) {
        return null;
    }
    @Shadow
    public CompoundTag getBlockEntityNbtForSaving(BlockPos arg0, Provider arg1) {
        return null;
    }
}
