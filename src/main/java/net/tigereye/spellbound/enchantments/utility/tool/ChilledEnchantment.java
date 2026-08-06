package net.tigereye.spellbound.enchantments.utility.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.Chilled.ChilledManager;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class ChilledEnchantment extends SBEnchantment{
    public ChilledEnchantment() {
        super(definition(ItemTags.MINING_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.chilled.RARITY), //enchantment weight
            Spellbound.config.chilled.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.chilled.BASE_POWER,Spellbound.config.chilled.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.chilled.BASE_POWER+Spellbound.config.chilled.POWER_RANGE,Spellbound.config.chilled.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.chilled.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.chilled.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.chilled.SOFT_CAP;}
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
