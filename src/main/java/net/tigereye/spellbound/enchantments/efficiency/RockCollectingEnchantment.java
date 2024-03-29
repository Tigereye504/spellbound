package net.tigereye.spellbound.enchantments.efficiency;

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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;

import java.util.*;
import java.util.stream.Stream;

public class RockCollectingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    private static final String ROCK_COLLECTOR_KEY = Spellbound.MODID+"RockCollector";
    private static final String UNIQUE_ROCK_COUNT_KEY = Spellbound.MODID+"UniqueRockCount";
    public RockCollectingEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.ROCK_COLLECTOR_ENABLED;
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
        if(isEnabled()) return 1;
        else return 0;
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

    /*@Override
    public void onActivate(int level, PlayerEntity player, ItemStack stack, Entity target) {
        if(!player.world.isClient && player.getPose() == EntityPose.CROUCHING) {
            List<Text> output = addTooltip(level, stack, player, null, 1000);
            output.forEach((line) -> player.sendMessage(line, false));
        }
    }*/

    @Override
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context) {
        return addTooltip(level,stack,player,context,10);
    }
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context, int maxsize) {
        List<Text> output = new ArrayList<>();
        NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
        Set<String> keys = tag.getKeys();
        Map<String,Integer> keyIntMap = new HashMap<>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_ROCK_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        int rockCount = tag.getInt(UNIQUE_ROCK_COUNT_KEY);
        output.add(new LiteralText(
                "--" + rockCount + " Unique Rocks (+"
                        +String.format("%.1f", calculateUniversalBonus(getUniqueRockCount(stack)))+")--"));
        Stream<Map.Entry<String, Integer>> stream = keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int scrollingSteps = Math.max(1,rockCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(player.world.getTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry));
        output.add(new LiteralText("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Text> output, Map.Entry<String, Integer> entry){
        output.add(new LiteralText(
                entry.getValue() + " ")
                .append(new TranslatableText(entry.getKey()))
                .append(" (+" + calculateBlockBonus(entry.getValue()) + ")"));
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof PickaxeItem
                || stack.getItem() instanceof ShovelItem
                || stack.getItem() == Items.BOOK;
    }

    private boolean hasRock(BlockState blockState, ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
        return tag.contains(blockState.getBlock().getTranslationKey());
    }

    private boolean addRock(BlockState blockState, LivingEntity miner, ItemStack stack){
        if(stack.getItem().isSuitableFor(blockState) || Spellbound.config.COLLECT_ANY_ROCK) {
            NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
            if (!hasRock(blockState, stack)) {
                tag.putInt(UNIQUE_ROCK_COUNT_KEY, tag.getInt(UNIQUE_ROCK_COUNT_KEY) + 1);
                tag.putInt(blockState.getBlock().getTranslationKey(), 1);
                if (miner instanceof PlayerEntity) {
                    String message = stack.getName().getString()
                            + " acquired a "
                            + new TranslatableText(blockState.getBlock().getTranslationKey()).getString()
                            + " fragment";
                    ((PlayerEntity) miner).sendMessage(new LiteralText(message)
                            , true);
                }
                return true;
            } else {
                int newValue = tag.getInt(blockState.getBlock().getTranslationKey()) + 1;
                tag.putInt(blockState.getBlock().getTranslationKey(), newValue);
                if (calculateBlockBonus(newValue - 1) < (calculateBlockBonus(newValue))) {
                    String message = stack.getName().getString()
                            + "'s "
                            + new TranslatableText(blockState.getBlock().getTranslationKey()).getString()
                            + " fragment improved";
                    ((PlayerEntity) miner).sendMessage(new LiteralText(message)
                            , true);
                }
                return false;
            }
        }
        return false;
    }

    private int getUniqueRockCount(ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
        return tag.getInt(UNIQUE_ROCK_COUNT_KEY);
    }

    private int getBlockRockCount(BlockState blockState, ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
        return tag.getInt(blockState.getBlock().getTranslationKey());
    }

    private float calculateUniversalBonus(int count){
        //return (float)Math.sqrt(count);
        return count/5.0F;
    }

    private int calculateBlockBonus(int count){
        int bonus = -1;
        while(count > 4){
            ++bonus;
            count /= 4;
        }
        return Math.max(bonus,0);

        //return (int)(Math.sqrt(count)/2);
    }
}
