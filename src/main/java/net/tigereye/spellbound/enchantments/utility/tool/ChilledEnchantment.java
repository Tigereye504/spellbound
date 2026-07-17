package net.tigereye.spellbound.enchantments.utility.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.Chilled.ChilledManager;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class ChilledEnchantment extends SBEnchantment{
    public ChilledEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.chilled.RARITY), EnchantmentCategory.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.chilled.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.chilled.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.chilled.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.chilled.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.chilled.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.chilled.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.chilled.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.chilled.IS_FOR_SALE;}

    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if (world.isClientSide()) {
            return;
        }
        chillBlock(world, pos.above());
        chillBlock(world, pos.below());
        chillBlock(world, pos.east());
        chillBlock(world, pos.west());
        chillBlock(world, pos.north());
        chillBlock(world, pos.south());
    }

    public void chillBlock(Level world, BlockPos pos){
        BlockState block = world.getBlockState(pos);
        ResourceLocation resultID = ChilledManager.getResult(block);
        if(resultID != null) {
            Block result = BuiltInRegistries.BLOCK.get(resultID);
            if(result != null){
                world.setBlockAndUpdate(pos,result.defaultBlockState());
            }
        }
    }
}
