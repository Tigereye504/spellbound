package net.tigereye.spellbound.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.BlendingData;
import net.minecraft.world.tick.BasicTickScheduler;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.TouchedBlocksPersistentState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(WorldChunk.class)
public class WorldChunkMixin extends Chunk {

    @Final
    @Shadow
    World world;
    public WorldChunkMixin(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biome, long inhabitedTime, @Nullable ChunkSection[] sectionArrayInitializer, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
    }


    @Inject(at = @At(value="HEAD"), method = "setBlockState")
    public void spellboundSetBlockStateMixin(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir){
        if(this.world != null && Spellbound.config.prospector.DETECT_ABUSE){
            if(world instanceof ServerWorld serverWorld && getInhabitedTime() > Spellbound.config.prospector.NEW_CHUNK_GRACE_PERIOD) {
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
    public NbtCompound getPackedBlockEntityNbt(BlockPos pos) {
        return null;
    }
    @Shadow
    public Stream<BlockPos> getLightSourcesStream() {
        return null;
    }
    @Shadow
    public BasicTickScheduler<Block> getBlockTickScheduler() {
        return null;
    }
    @Shadow
    public BasicTickScheduler<Fluid> getFluidTickScheduler() {
        return null;
    }
    @Shadow
    public TickSchedulers getTickSchedulers() {
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
}
