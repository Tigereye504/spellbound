package net.tigereye.spellbound.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import net.tigereye.spellbound.registration.SBComponents;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import java.util.List;

public class CrateBlock extends BaseEntityBlock {
    public static final MapCodec<CrateBlock> CODEC = simpleCodec(CrateBlock::new);

    @Override
    public MapCodec<CrateBlock> codec() {
        return CODEC;
    }

    public CrateBlock(Properties settings) {
        super(settings);
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CrateBlockEntity(null,0,pos,state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.getBlockEntity(pos) instanceof CrateBlockEntity crateBlockEntity) {
            DataComponentMap components = itemStack.getComponents();
            int crateQuality = components.getOrDefault(SBComponents.CRATE_QUALITY,0);
            String crateDimension = components.getOrDefault(SBComponents.CRATE_DIMENSION,null);
            crateBlockEntity.setQuality(crateQuality);
            if(crateDimension != null){
                crateBlockEntity.setDimension(new ResourceLocation(crateDimension));
            }
        }
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof CrateBlockEntity crateBlockEntity && !EnchantmentHelper.hasSilkTouch(player.getMainHandItem())) {
            crateBlockEntity.spawnLoot(world, pos, player);
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
        DataComponentMap components = itemStack.getComponents();
        int crateQuality = components.getOrDefault(SBComponents.CRATE_QUALITY,0);
        String crateDimension = components.getOrDefault(SBComponents.CRATE_DIMENSION,null);

        tooltip.add(Component.translatable("crate.spellbound.quality"+crateQuality));
        if(crateDimension != null) {
            ResourceLocation dimension = new ResourceLocation(crateDimension);
            tooltip.add(Component.translatable("crate.dimension."+dimension.toLanguageKey()));
        }
    }
    //TODO: see fabricmc.net/wiki/tutorial:tooltip when updating to 1.21.5

}
