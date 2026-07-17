package net.tigereye.spellbound.blocks.entity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.data.SunkenTreasure.SunkenTreasureManager;
import net.tigereye.spellbound.registration.SBItems;
import org.jetbrains.annotations.Nullable;

public class CrateBlockEntity extends BlockEntity {
    public static final String LOOT_DIMENSION_KEY = "LootDimension";
    public static final String LOOT_QUALITY_KEY = "LootQuality";
    ResourceLocation dimension;
    int quality;

    public CrateBlockEntity(ResourceLocation dimension, int quality, BlockPos blockPos, BlockState blockState) {
        super(SBItems.CRATE_BLOCK_ENTITY, blockPos, blockState);
        this.dimension = dimension;
        this.quality = quality;
    }

    public CrateBlockEntity(BlockPos blockPos, BlockState blockState) {
        this(null, 0, blockPos, blockState);
    }

    public void spawnLoot(Level world, BlockPos pos, @Nullable Player player) {
        if (this.level != null && this.level.getServer() != null) {
            LootParams.Builder builder = new LootParams.Builder((ServerLevel)this.level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition));
            if(player != null){
                builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
            }
            ResourceLocation lootTableId = SunkenTreasureManager.getWeightedRandomLootTableId(quality,dimension,this.level.getRandom());
            LootTable lootTable = this.level.getServer().getLootData().getLootTable(lootTableId);
            if (player instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)player, lootTableId);
            }
            lootTable.getRandomItems(builder.create(LootContextParamSets.CHEST), 0,
                    (itemStack) -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), itemStack));
        }
    }

    public void setQuality(int quality){
        this.quality = quality;
    }
    public void setDimension(ResourceLocation dimension){
        this.dimension = dimension;
    }
    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        if (nbt.contains(LOOT_DIMENSION_KEY, Tag.TAG_STRING)) {
            this.dimension = new ResourceLocation(nbt.getString(LOOT_DIMENSION_KEY));
        }
        if (nbt.contains(LOOT_QUALITY_KEY, Tag.TAG_INT)) {
            this.quality = nbt.getInt(LOOT_QUALITY_KEY);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        if(dimension != null) {
            nbt.putString(LOOT_DIMENSION_KEY, dimension.toString());
        }
        nbt.putInt(LOOT_QUALITY_KEY,quality);
    }
}
