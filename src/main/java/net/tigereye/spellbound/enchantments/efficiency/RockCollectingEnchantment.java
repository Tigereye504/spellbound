package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.components.RockCollectionComponent;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBItems;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;
import java.util.stream.Stream;

public class RockCollectingEnchantment extends SBEnchantment{

    public static final String ROCK_COLLECTOR_KEY = Spellbound.MODID+":rock_collector";
    public static final String UNIQUE_ROCK_COUNT_KEY = Spellbound.MODID+":unique_rock_count";
    public RockCollectingEnchantment() {
        super(definition(ItemTags.MINING_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.rockCollector.RARITY), //enchantment weight
            Spellbound.config.rockCollector.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.rockCollector.BASE_POWER,Spellbound.config.rockCollector.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.rockCollector.BASE_POWER+Spellbound.config.rockCollector.POWER_RANGE,Spellbound.config.rockCollector.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.rockCollector.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.rockCollector.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.rockCollector.SOFT_CAP;}
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
        if(!itemStack.has(SBComponents.ROCK_COLECTION)){
            return;
        }
        ItemStack bagOfRocks = new ItemStack(SBItems.BAG_OF_ROCKS);
        bagOfRocks.set(SBComponents.ROCK_COLECTION, itemStack.get(SBComponents.ROCK_COLECTION));
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
        Map<Holder<Block>,Integer> keyIntMap = getRockMap(stack);
        int rockCount = getUniqueRockCount(stack);
        output.add(Component.literal(
                "--" + rockCount + " Unique Rocks (+"
                        +String.format("%.1f", calculateUniversalBonus(getUniqueRockCount(stack)))+")--"));
        Stream<Map.Entry<Holder<Block>, Integer>> stream = keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int scrollingSteps = Math.max(1,rockCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(world.getGameTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry));
        output.add(Component.literal("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Component> output, Map.Entry<Holder<Block>, Integer> entry){
        output.add(Component.literal(
                entry.getValue() + " ")
                .append(entry.getKey().getRegisteredName())
                .append(" (+" + calculateBlockBonus(entry.getValue()) + ")"));
    }

    private boolean hasRock(BlockState blockState, ItemStack stack){
        if(!stack.has(SBComponents.ROCK_COLECTION)){
            return false;
        }
        return stack.get(SBComponents.ROCK_COLECTION).hasRock(blockState.getBlockHolder());
    }

    //returns 'true' if a new type of rock is collected
    private void addRock(BlockState blockState, BlockPos pos, Level world,LivingEntity miner, ItemStack stack){
        if((stack.isCorrectToolForDrops(blockState) || Spellbound.config.COLLECT_ANY_ROCK) && blockState.isCollisionShapeFullBlock(world,pos)) {
            if (!hasRock(blockState, stack)) {
                //if there is no component, make a new one with a rock
                if(!stack.has(SBComponents.ROCK_COLECTION)){
                    stack.set(SBComponents.ROCK_COLECTION,RockCollectionComponent.ofRock(blockState.getBlockHolder()));
                }
                //else add a new rock to the existing collection
                else{
                    stack.set(SBComponents.ROCK_COLECTION,stack.get(SBComponents.ROCK_COLECTION).withRockAdded(blockState.getBlockHolder()));
                }
                //announce the new rock
                if (miner instanceof Player) {
                    String message = stack.getHoverName().getString()
                            + " acquired a "
                            + Component.translatable(blockState.getBlock().getDescriptionId()).getString()
                            + " fragment";
                    ((Player) miner).displayClientMessage(Component.literal(message)
                            , true);
                }
                return;
            } else {
                RockCollectionComponent rcc = stack.get(SBComponents.ROCK_COLECTION);
                int newValue = rcc.getRockCopies(blockState.getBlockHolder()) + 1;
                stack.set(SBComponents.ROCK_COLECTION,rcc.withRockAdded(blockState.getBlockHolder()));
                if (calculateBlockBonus(newValue - 1) < (calculateBlockBonus(newValue))) {
                    String message = stack.getHoverName().getString()
                            + "'s "
                            + Component.translatable(blockState.getBlock().getDescriptionId()).getString()
                            + " fragment improved";
                    ((Player) miner).displayClientMessage(Component.literal(message)
                            , true);
                }
                return;
            }
        }
        return;
    }

    private int getUniqueRockCount(ItemStack stack){
        if(stack.has(SBComponents.ROCK_COLECTION)){
            return stack.get(SBComponents.ROCK_COLECTION).rocks().size();
        }
        return 0;
    }

    public Map<Holder<Block>,Integer> getRockMap(ItemStack stack){
        if(!stack.has(SBComponents.ROCK_COLECTION)){
            return new HashMap<>();
        }
        RockCollectionComponent collection = stack.get(SBComponents.ROCK_COLECTION);
        Map<Holder<Block>,Integer> map = new HashMap<>();
        collection.rocks().forEach((entry) -> map.put(entry.block(), entry.count()));
        return map;
    }

    private int getBlockRockCount(BlockState blockState, ItemStack stack){
        if(!stack.has(SBComponents.ROCK_COLECTION)){
            return 0;
        }
        RockCollectionComponent rcc = stack.get(SBComponents.ROCK_COLECTION);
        return rcc.getRockCopies(blockState.getBlockHolder());
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
