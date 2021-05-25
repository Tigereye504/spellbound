package net.tigereye.spellbound.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;

import java.util.*;

public class RockCollectorEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    private static final String ROCK_COLLECTOR_KEY = Spellbound.MODID+"RockCollector";
    private static final String UNIQUE_ROCK_COUNT_KEY = Spellbound.MODID+"UniqueRockCount";
    public RockCollectorEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack)
                ||EnchantmentTarget.DIGGER.isAcceptableItem(stack.getItem());
    }

    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        float UniqueRockSpeed = 0.0F;
        int BlockRockSpeed = 0;
        if(miningSpeed > 1.0F) {
            UniqueRockSpeed = calculateUniversalBonus(getUniqueRockCount(stack));
            BlockRockSpeed = calculateBlockBonus(getBlockRockCount(block,stack));
        }
        return miningSpeed + UniqueRockSpeed + BlockRockSpeed;
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        addRock(state,player,stack);
    }

    @Override
    public void onActivate(int level, PlayerEntity player, ItemStack stack, Entity target) {
        if(!player.world.isClient && player.getPose() == EntityPose.CROUCHING) {
            List<Text> output = addTooltip(level, stack, player, null, 1000);
            output.forEach((line) -> player.sendMessage(line, false));
        }
    }

    @Override
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context) {
        return addTooltip(level,stack,player,context,10);
    }
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context, int maxsize) {
        List<Text> output = new ArrayList<>();
        CompoundTag tag = stack.getOrCreateSubTag(ROCK_COLLECTOR_KEY);
        Set<String> keys = tag.getKeys();
        Map<String,Integer> keyIntMap = new HashMap<>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_ROCK_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        output.add(new LiteralText(
                "--" + tag.getInt(UNIQUE_ROCK_COUNT_KEY) + " Unique Rocks (+"
                        +String.format("%.1f", calculateUniversalBonus(getUniqueRockCount(stack)))+")--"));
        keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(maxsize)
                .forEach((entry) -> output.add(new LiteralText(
                            entry.getValue() + " ")
                            .append(new TranslatableText(entry.getKey()))
                            .append(" (+" + calculateBlockBonus(entry.getValue()) + ")")));
        output.add(new LiteralText("--------------------------"));
        return output;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    //I want to disallow efficiency and its knockoffs
    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.EFFICIENCY);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem
                || stack.getItem() instanceof ShovelItem
                || stack.getItem() == Items.BOOK;
    }

    private boolean hasRock(BlockState blockState, ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(ROCK_COLLECTOR_KEY);
        return tag.contains(blockState.getBlock().getTranslationKey());
    }

    private boolean addRock(BlockState blockState, LivingEntity miner, ItemStack stack){
        if(!stack.getItem().isEffectiveOn(blockState)){
            return false;
        }
        CompoundTag tag = stack.getOrCreateSubTag(ROCK_COLLECTOR_KEY);
        if(!hasRock(blockState,stack)){
            tag.putInt(UNIQUE_ROCK_COUNT_KEY,tag.getInt(UNIQUE_ROCK_COUNT_KEY)+1);
            tag.putInt(blockState.getBlock().getTranslationKey(),1);
            if(miner instanceof PlayerEntity){
                String message = stack.getName().getString()
                        + " acquired a "
                        + new TranslatableText(blockState.getBlock().getTranslationKey()).getString()
                        + " fragment";
                ((PlayerEntity)miner).sendMessage(new LiteralText(message)
                        , true);
            }
            return true;
        }
        else{
            int newValue = tag.getInt(blockState.getBlock().getTranslationKey())+1;
            tag.putInt(blockState.getBlock().getTranslationKey(),newValue);
            if(calculateBlockBonus(newValue-1) < (calculateBlockBonus(newValue))){
                String message = stack.getName().getString()
                        + "'s "
                        + new TranslatableText(blockState.getBlock().getTranslationKey()).getString()
                        + " fragment improved";
                ((PlayerEntity)miner).sendMessage(new LiteralText(message)
                        , true);
            }
            return false;
        }
    }

    private int getUniqueRockCount(ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(ROCK_COLLECTOR_KEY);
        return tag.getInt(UNIQUE_ROCK_COUNT_KEY);
    }

    private int getBlockRockCount(BlockState blockState, ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(ROCK_COLLECTOR_KEY);
        return tag.getInt(blockState.getBlock().getTranslationKey());
    }

    private float calculateUniversalBonus(int count){
        //return (float)Math.sqrt(count);
        return count/5.0F;
    }

    private int calculateBlockBonus(int count){
        return (int)(Math.sqrt(count)/2);
    }
}
