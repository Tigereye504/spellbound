package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBItems;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;
import java.util.stream.Stream;

public class RockCollectingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public static final String ROCK_COLLECTOR_KEY = Spellbound.MODID+"RockCollector";
    public static final String UNIQUE_ROCK_COUNT_KEY = Spellbound.MODID+"UniqueRockCount";
    public RockCollectingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.ROCK_COLLECTOR_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.ROCK_COLLECTOR_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.ROCK_COLLECTOR_POWER_PER_RANK * level) + Spellbound.config.ROCK_COLLECTOR_BASE_POWER;
        if(level > Spellbound.config.ROCK_COLLECTOR_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.ROCK_COLLECTOR_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.ROCK_COLLECTOR_HARD_CAP;
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

    @Override
    public void onLegacyToolBreak(int level, ItemStack book, ItemStack itemStack, PlayerEntity entity) {
        ItemStack bagOfRocks = new ItemStack(SBItems.BAG_OF_ROCKS);
        bagOfRocks.setSubNbt(ROCK_COLLECTOR_KEY, itemStack.getSubNbt(ROCK_COLLECTOR_KEY));
        if(!entity.giveItemStack(bagOfRocks)){
            entity.dropStack(bagOfRocks,0.5f);
        }
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
        return addTooltip(stack,player.world);
    }
    public List<Text> addTooltip(ItemStack stack, World world) {
        List<Text> output = new ArrayList<>();
        NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
        Map<String,Integer> keyIntMap = getRockMap(stack);
        int rockCount = tag.getInt(UNIQUE_ROCK_COUNT_KEY);
        output.add(Text.literal(
                "--" + rockCount + " Unique Rocks (+"
                        +String.format("%.1f", calculateUniversalBonus(getUniqueRockCount(stack)))+")--"));
        Stream<Map.Entry<String, Integer>> stream = keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int scrollingSteps = Math.max(1,rockCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(world.getTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry));
        output.add(Text.literal("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Text> output, Map.Entry<String, Integer> entry){
        output.add(Text.literal(
                entry.getValue() + " ")
                .append(Text.translatable(entry.getKey()))
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
                            + Text.translatable(blockState.getBlock().getTranslationKey()).getString()
                            + " fragment";
                    ((PlayerEntity) miner).sendMessage(Text.literal(message)
                            , true);
                }
                return true;
            } else {
                int newValue = tag.getInt(blockState.getBlock().getTranslationKey()) + 1;
                tag.putInt(blockState.getBlock().getTranslationKey(), newValue);
                if (calculateBlockBonus(newValue - 1) < (calculateBlockBonus(newValue))) {
                    String message = stack.getName().getString()
                            + "'s "
                            + Text.translatable(blockState.getBlock().getTranslationKey()).getString()
                            + " fragment improved";
                    ((PlayerEntity) miner).sendMessage(Text.literal(message)
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

    public Map<String,Integer> getRockMap(ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(ROCK_COLLECTOR_KEY);
        Set<String> keys = tag.getKeys();
        Map<String,Integer> keyIntMap = new HashMap<>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_ROCK_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        return keyIntMap;
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
