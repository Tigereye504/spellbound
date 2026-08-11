package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.HashSet;
import java.util.Set;

public class WidenedEnchantment extends SBEnchantment {

    public WidenedEnchantment() {
        super(definition(ItemTags.MINING_ENCHANTABLE,
            Spellbound.config.widened.WEIGHT, //enchantment weight
            Spellbound.config.widened.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.widened.BASE_POWER,Spellbound.config.widened.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.widened.BASE_POWER+Spellbound.config.widened.POWER_RANGE,Spellbound.config.widened.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.widened.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.widened.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.widened.SOFT_CAP;}
    public boolean isTreasureOnly() {return Spellbound.config.widened.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.widened.IS_FOR_SALE;}

    @Override
    public float getMiningSpeed(int level, Player playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        return miningSpeed*Spellbound.config.widened.MINING_SPEED_FACTOR;
    }

    @Override
    public void onBreakBlockDirectly(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if(state.getBlock().defaultDestroyTime() == 0 || !(Spellbound.config.widened.ALLOW_UNSUITABLE_TOOL || stack.isCorrectToolForDrops(state))){
            return;
        }
        breakWidenedArea(level,stack,world,pos,state,player);
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if(state.getBlock().defaultDestroyTime() == 0){
            return;
        }
        stack.mineBlock(world,state,pos,player);
    }

    public void onItemUse(int level, ItemStack itemStack, UseOnContext context, InteractionResult result) {
        if(result.consumesAction()){
            useWidenedArea(level,itemStack,context);
        }
    }

    private void breakWidenedArea(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player){
        player.pick(10,1,false);

        Vec3 cameraPos = player.getEyePosition(1);
        Vec3 rotation = player.getViewVector(1);
        Vec3 combined = cameraPos.add(rotation.x * 10, rotation.y * 10, rotation.z * 10);

        BlockHitResult blockHitResult = world.clip(new ClipContext(cameraPos, combined, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if(blockHitResult.getType() == HitResult.Type.BLOCK){
            Direction dir = blockHitResult.getDirection();
            Set<BlockPos> positions = findBlocksInRange(pos,dir,level);
            positions = validateBlocks(positions,world,state,dir,pos,stack);
            breakBlocksInWorld(positions,world,stack,player);
        }
    }
    private Set<BlockPos> findBlocksInRange(BlockPos pos, Direction dir, int level){
        Set<BlockPos> positions = new HashSet<>();
        Vec3i x;
        Vec3i z;
        switch (dir){
            case UP, DOWN:
                x = Direction.NORTH.getNormal();
                z = Direction.EAST.getNormal();
                break;
            case EAST, WEST:
                x = Direction.NORTH.getNormal();
                z = Direction.UP.getNormal();
                break;
            default: //NORTH, SOUTH:
                x = Direction.UP.getNormal();
                z = Direction.EAST.getNormal();
        }
        int radius = 1;
        int width = 0;
        for (int i = 0; i < level; i++) {
            addLevelOfBlocks(pos,positions,radius,width,x,z);
            if(width >= radius){
                radius++;
                width = 0;
            }
            else{
                width++;
            }
        }
        return positions;
    }

    private void addLevelOfBlocks(BlockPos pos, Set<BlockPos> positions,int radius, int width, Vec3i x, Vec3i z){
        if(width == 0){
            positions.add(pos.offset(x.multiply(radius)));
            positions.add(pos.offset(x.multiply(-radius)));
            positions.add(pos.offset(z.multiply(radius)));
            positions.add(pos.offset(z.multiply(-radius)));
        }
        else{
            positions.add(pos.offset(x.multiply(radius)).offset(z.multiply(width)));
            positions.add(pos.offset(x.multiply(-radius)).offset(z.multiply(width)));
            positions.add(pos.offset(z.multiply(radius)).offset(x.multiply(width)));
            positions.add(pos.offset(z.multiply(-radius)).offset(x.multiply(width)));
            positions.add(pos.offset(x.multiply(radius)).offset(z.multiply(-width)));
            positions.add(pos.offset(x.multiply(-radius)).offset(z.multiply(-width)));
            positions.add(pos.offset(z.multiply(radius)).offset(x.multiply(-width)));
            positions.add(pos.offset(z.multiply(-radius)).offset(x.multiply(-width)));
        }
    }
    private Set<BlockPos> validateBlocks(Set<BlockPos> positions, Level world, BlockState state, Direction dir, BlockPos origin, ItemStack tool){
        positions = getMatchingBlocks(positions, world, state, origin, tool);
        if(Spellbound.config.widened.REQUIRE_UNCOVERED_BLOCK){
            positions = getUncoveredBlocks(positions, world, dir);
        }
        if(Spellbound.config.widened.REQUIRE_CONTIGUOUS_BREAK){
            positions = getConnectedBlocksInSet(positions,origin,dir);
        }
        return positions;
    }

    private Set<BlockPos> getMatchingBlocks(Set<BlockPos> positions, Level world, BlockState state, BlockPos origin, ItemStack tool){
        positions= new HashSet<>(positions);
        positions.removeIf((blockPos) -> {
            boolean MismatchedBlock;
            if(Spellbound.config.widened.REQUIRE_EXACT_MATCHING_BLOCK){
                MismatchedBlock = (world.getBlockState(blockPos).getBlock() != state.getBlock());
            }
            else{
                BlockState targetState = world.getBlockState(blockPos);
                float targetHardness = targetState.getDestroySpeed(world,blockPos);
                float hardnessDifference = state.getDestroySpeed(world,origin) - targetHardness;
                MismatchedBlock = !(Spellbound.config.widened.ALLOW_UNSUITABLE_TOOL || tool.isCorrectToolForDrops(targetState))
                        || targetHardness == 0
                        || hardnessDifference < Spellbound.config.widened.MAXIMUM_HARDNESS_GAIN
                        || hardnessDifference > Spellbound.config.widened.MAXIMUM_HARDNESS_LOSS;
            }return MismatchedBlock;
        });
        return positions;
    }

    private Set<BlockPos> getUncoveredBlocks(Set<BlockPos> positions, Level world, Direction dir) {
        positions = new HashSet<>(positions);
        positions.removeIf((blockPos) -> world.getBlockState(blockPos.offset(dir.getNormal())).isRedstoneConductor(world,blockPos.offset(dir.getNormal())));
        return positions;
    }

    private Set<BlockPos> getConnectedBlocksInSet(Set<BlockPos> positions, BlockPos origin, Direction dir) {
        positions = new HashSet<>(positions);
        Vec3i x;
        Vec3i z;
        switch (dir){
            case UP, DOWN:
                x = Direction.NORTH.getNormal();
                z = Direction.EAST.getNormal();
                break;
            case EAST, WEST:
                x = Direction.NORTH.getNormal();
                z = Direction.UP.getNormal();
                break;
            default: //NORTH, SOUTH:
                x = Direction.UP.getNormal();
                z = Direction.EAST.getNormal();
        }
        Set<BlockPos> connectedBlocks = new HashSet<>();
        return getConnectedBlocksInSet(positions,origin,x,z,connectedBlocks);
    }

    private Set<BlockPos> getConnectedBlocksInSet(Set<BlockPos> positions, BlockPos origin, Vec3i x, Vec3i z,Set<BlockPos> connectedBlocks) {
        BlockPos target = origin.offset(x);
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        target = origin.offset(z);
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        target = origin.offset(x.multiply(-1));
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        target = origin.offset(z.multiply(-1));
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        if(Spellbound.config.widened.IS_DIAGONAL_CONTIGUOUS){
            target = origin.offset(x).offset(z);
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
            target = origin.offset(x).offset(z.multiply(-1));
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
            target = origin.offset(x.multiply(-1).offset(z));
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
            target = origin.offset(x.multiply(-1)).offset(z.multiply(-1));
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
        }
        return connectedBlocks;
    }

    private void breakBlocksInWorld(Set<BlockPos> positions, Level world, ItemStack tool, Player player){
        for (BlockPos blockPos : positions) {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (blockState.isAir()) continue; //shouldn't be needed due to validation step, but not a bad idea even so.
            world.getProfiler().push("explosion_blocks");
            if (world instanceof ServerLevel) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
                Block.dropResources(blockState,world,blockPos,blockEntity,player,tool);
            }
            world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
            world.getProfiler().pop();
            SBEnchantmentHelper.onBreakBlock(block,world,blockPos,blockState,player);
        }
    }


    private void useWidenedArea(int level, ItemStack stack, UseOnContext context){
        Player player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.pick(10,1,false);

        Vec3 cameraPos = player.getEyePosition(1);
        Vec3 rotation = player.getViewVector(1);
        Vec3 combined = cameraPos.add(rotation.x * 10, rotation.y * 10, rotation.z * 10);

        BlockHitResult blockHitResult = context.getLevel().clip(new ClipContext(cameraPos, combined, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if(blockHitResult.getType() == HitResult.Type.BLOCK){
            Direction dir = blockHitResult.getDirection();
            Set<BlockPos> positions = findBlocksInRange(context.getClickedPos(),dir,level);
            if(Spellbound.config.widened.REQUIRE_UNCOVERED_BLOCK){
                positions = getUncoveredBlocks(positions, context.getLevel(), dir);
            }
            if(Spellbound.config.widened.REQUIRE_CONTIGUOUS_BREAK){
                positions = getConnectedBlocksInSet(positions,context.getClickedPos(),dir);
            }
            useOnBlocksInWorld(positions,stack,context);
        }
    }

    private void useOnBlocksInWorld(Set<BlockPos> positions, ItemStack stack, UseOnContext context){
        for (BlockPos blockPos:
             positions) {
            Player playerEntity = context.getPlayer();
            BlockPos blockOffset = blockPos.subtract(context.getClickedPos());
            UseOnContext newContext = new UseOnContext(context.getLevel(),context.getPlayer(),context.getHand(),stack,
                    new BlockHitResult(context.getClickLocation().add(blockOffset.getX(),blockOffset.getY(),blockOffset.getZ()),
                            context.getClickedFace(),blockPos,context.isInside()));
            BlockInWorld cachedBlockPosition = new BlockInWorld(newContext.getLevel(), blockPos, false);
            if (playerEntity != null && (!playerEntity.getAbilities().mayBuild || !stack.canBreakBlockInAdventureMode(cachedBlockPosition))) {
                return;
            }
            Item item = stack.getItem();

            if(Spellbound.DEBUG){
                Block outputBlock = context.getLevel().getBlockState(newContext.getClickedPos()).getBlock();
                Spellbound.LOGGER.info("Widened enchantment using "+stack.getHoverName().getString()+" on block "+outputBlock+" at ");
                Spellbound.LOGGER.info("x = "+newContext.getClickedPos().getX()+" y = "+newContext.getClickedPos().getY()+" z = "+newContext.getClickedPos().getZ());
            }
            InteractionResult actionResult = item.useOn(newContext);
            if (playerEntity != null && actionResult.indicateItemUse()) {
                playerEntity.awardStat(Stats.ITEM_USED.get(item));
            }
        }
    }
}
