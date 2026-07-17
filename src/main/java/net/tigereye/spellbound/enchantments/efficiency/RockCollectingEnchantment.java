package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBItems;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;
import java.util.stream.Stream;

public class RockCollectingEnchantment extends SBEnchantment{

    public static final String ROCK_COLLECTOR_KEY = Spellbound.MODID+"RockCollector";
    public static final String UNIQUE_ROCK_COUNT_KEY = Spellbound.MODID+"UniqueRockCount";
    public RockCollectingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.rockCollector.RARITY), EnchantmentCategory.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.rockCollector.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.rockCollector.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.rockCollector.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.rockCollector.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.rockCollector.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.rockCollector.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.rockCollector.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.rockCollector.IS_FOR_SALE;}

    @Override
    public float getMiningSpeed(int level, Player playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        float UniqueRockSpeed = 0.0F;
        int BlockRockSpeed = 0;
        if(miningSpeed > 1.0F) {
            UniqueRockSpeed = calculateUniversalBonus(getUniqueRockCount(stack));
            BlockRockSpeed = calculateBlockBonus(getBlockRockCount(block,stack));
        }
        return miningSpeed + UniqueRockSpeed + BlockRockSpeed;
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        addRock(state,pos,world,player,stack);
    }

    @Override
    public void onLegacyToolBreak(int level, ItemStack book, ItemStack itemStack, Entity entity) {
        ItemStack bagOfRocks = new ItemStack(SBItems.BAG_OF_ROCKS);
        bagOfRocks.addTagElement(ROCK_COLLECTOR_KEY, itemStack.getTagElement(ROCK_COLLECTOR_KEY));
        if(entity instanceof Player pEntity) {
            if (!pEntity.addItem(bagOfRocks)) {
                entity.spawnAtLocation(bagOfRocks, 0.5f);
            }
        }
        else{
            entity.spawnAtLocation(bagOfRocks, 0.5f);
        }
    }

    @Override
    public List<Component> addTooltip(int level, ItemStack stack, Player player, TooltipFlag context) {
        return addTooltip(stack,player.level());
    }
    public List<Component> addTooltip(ItemStack stack, Level world) {
        List<Component> output = new ArrayList<>();
        CompoundTag tag = stack.getOrCreateTagElement(ROCK_COLLECTOR_KEY);
        Map<String,Integer> keyIntMap = getRockMap(stack);
        int rockCount = tag.getInt(UNIQUE_ROCK_COUNT_KEY);
        output.add(Component.literal(
                "--" + rockCount + " Unique Rocks (+"
                        +String.format("%.1f", calculateUniversalBonus(getUniqueRockCount(stack)))+")--"));
        Stream<Map.Entry<String, Integer>> stream = keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int scrollingSteps = Math.max(1,rockCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(world.getGameTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry));
        output.add(Component.literal("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Component> output, Map.Entry<String, Integer> entry){
        output.add(Component.literal(
                entry.getValue() + " ")
                .append(Component.translatable(entry.getKey()))
                .append(" (+" + calculateBlockBonus(entry.getValue()) + ")"));
    }

    private boolean hasRock(BlockState blockState, ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(ROCK_COLLECTOR_KEY);
        return tag.contains(blockState.getBlock().getDescriptionId());
    }

    private boolean addRock(BlockState blockState, BlockPos pos, Level world,LivingEntity miner, ItemStack stack){
        if((stack.isCorrectToolForDrops(blockState) || Spellbound.config.COLLECT_ANY_ROCK) && blockState.isCollisionShapeFullBlock(world,pos)) {
            CompoundTag tag = stack.getOrCreateTagElement(ROCK_COLLECTOR_KEY);
            if (!hasRock(blockState, stack)) {
                tag.putInt(UNIQUE_ROCK_COUNT_KEY, tag.getInt(UNIQUE_ROCK_COUNT_KEY) + 1);
                tag.putInt(blockState.getBlock().getDescriptionId(), 1);
                if (miner instanceof Player) {
                    String message = stack.getHoverName().getString()
                            + " acquired a "
                            + Component.translatable(blockState.getBlock().getDescriptionId()).getString()
                            + " fragment";
                    ((Player) miner).displayClientMessage(Component.literal(message)
                            , true);
                }
                return true;
            } else {
                int newValue = tag.getInt(blockState.getBlock().getDescriptionId()) + 1;
                tag.putInt(blockState.getBlock().getDescriptionId(), newValue);
                if (calculateBlockBonus(newValue - 1) < (calculateBlockBonus(newValue))) {
                    String message = stack.getHoverName().getString()
                            + "'s "
                            + Component.translatable(blockState.getBlock().getDescriptionId()).getString()
                            + " fragment improved";
                    ((Player) miner).displayClientMessage(Component.literal(message)
                            , true);
                }
                return false;
            }
        }
        return false;
    }

    private int getUniqueRockCount(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(ROCK_COLLECTOR_KEY);
        return tag.getInt(UNIQUE_ROCK_COUNT_KEY);
    }

    public Map<String,Integer> getRockMap(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(ROCK_COLLECTOR_KEY);
        Set<String> keys = tag.getAllKeys();
        Map<String,Integer> keyIntMap = new HashMap<>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_ROCK_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        return keyIntMap;
    }

    private int getBlockRockCount(BlockState blockState, ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(ROCK_COLLECTOR_KEY);
        return tag.getInt(blockState.getBlock().getDescriptionId());
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
