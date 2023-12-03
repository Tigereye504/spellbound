package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.HashSet;
import java.util.Set;

public class WidenedEnchantment extends SBEnchantment {

    public WidenedEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.widened.RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.widened.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.widened.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.widened.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.widened.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.widened.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.widened.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.widened.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.widened.IS_FOR_SALE;}

    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        return miningSpeed*Spellbound.config.widened.MINING_SPEED_FACTOR;
    }

    @Override
    public void onBreakBlockDirectly(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.getBlock().getHardness() == 0 || !(Spellbound.config.widened.ALLOW_UNSUITABLE_TOOL || stack.isSuitableFor(state))){
            return;
        }
        breakWidenedArea(level,stack,world,pos,state,player);
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.getBlock().getHardness() == 0){
            return;
        }
        stack.postMine(world,state,pos,player);
    }

    public void onItemUse(int level, ItemStack itemStack, ItemUsageContext context, ActionResult result) {
        if(result.isAccepted()){
            useWidenedArea(level,itemStack,context);
        }
    }

    private void breakWidenedArea(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player){
        player.raycast(10,1,false);

        Vec3d cameraPos = player.getCameraPosVec(1);
        Vec3d rotation = player.getRotationVec(1);
        Vec3d combined = cameraPos.add(rotation.x * 10, rotation.y * 10, rotation.z * 10);

        BlockHitResult blockHitResult = world.raycast(new RaycastContext(cameraPos, combined, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));

        if(blockHitResult.getType() == HitResult.Type.BLOCK){
            Direction dir = blockHitResult.getSide();
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
                x = Direction.NORTH.getVector();
                z = Direction.EAST.getVector();
                break;
            case EAST, WEST:
                x = Direction.NORTH.getVector();
                z = Direction.UP.getVector();
                break;
            default: //NORTH, SOUTH:
                x = Direction.UP.getVector();
                z = Direction.EAST.getVector();
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
            positions.add(pos.add(x.multiply(radius)));
            positions.add(pos.add(x.multiply(-radius)));
            positions.add(pos.add(z.multiply(radius)));
            positions.add(pos.add(z.multiply(-radius)));
        }
        else{
            positions.add(pos.add(x.multiply(radius)).add(z.multiply(width)));
            positions.add(pos.add(x.multiply(-radius)).add(z.multiply(width)));
            positions.add(pos.add(z.multiply(radius)).add(x.multiply(width)));
            positions.add(pos.add(z.multiply(-radius)).add(x.multiply(width)));
            positions.add(pos.add(x.multiply(radius)).add(z.multiply(-width)));
            positions.add(pos.add(x.multiply(-radius)).add(z.multiply(-width)));
            positions.add(pos.add(z.multiply(radius)).add(x.multiply(-width)));
            positions.add(pos.add(z.multiply(-radius)).add(x.multiply(-width)));
        }
    }
    private Set<BlockPos> validateBlocks(Set<BlockPos> positions, World world, BlockState state, Direction dir, BlockPos origin, ItemStack tool){
        positions = getMatchingBlocks(positions, world, state, origin, tool);
        if(Spellbound.config.widened.REQUIRE_UNCOVERED_BLOCK){
            positions = getUncoveredBlocks(positions, world, dir);
        }
        if(Spellbound.config.widened.REQUIRE_CONTIGUOUS_BREAK){
            positions = getConnectedBlocksInSet(positions,origin,dir);
        }
        return positions;
    }

    private Set<BlockPos> getMatchingBlocks(Set<BlockPos> positions, World world, BlockState state, BlockPos origin, ItemStack tool){
        positions= new HashSet<>(positions);
        positions.removeIf((blockPos) -> {
            boolean MismatchedBlock;
            if(Spellbound.config.widened.REQUIRE_EXACT_MATCHING_BLOCK){
                MismatchedBlock = (world.getBlockState(blockPos).getBlock() != state.getBlock());
            }
            else{
                BlockState targetState = world.getBlockState(blockPos);
                float targetHardness = targetState.getHardness(world,blockPos);
                float hardnessDifference = state.getHardness(world,origin) - targetHardness;
                MismatchedBlock = !(Spellbound.config.widened.ALLOW_UNSUITABLE_TOOL || tool.isSuitableFor(targetState))
                        || targetHardness == 0
                        || hardnessDifference < Spellbound.config.widened.MAXIMUM_HARDNESS_GAIN
                        || hardnessDifference > Spellbound.config.widened.MAXIMUM_HARDNESS_LOSS;
            }return MismatchedBlock;
        });
        return positions;
    }

    private Set<BlockPos> getUncoveredBlocks(Set<BlockPos> positions, World world, Direction dir) {
        positions = new HashSet<>(positions);
        positions.removeIf((blockPos) -> world.getBlockState(blockPos.add(dir.getVector())).isSolidBlock(world,blockPos.add(dir.getVector())));
        return positions;
    }

    private Set<BlockPos> getConnectedBlocksInSet(Set<BlockPos> positions, BlockPos origin, Direction dir) {
        positions = new HashSet<>(positions);
        Vec3i x;
        Vec3i z;
        switch (dir){
            case UP, DOWN:
                x = Direction.NORTH.getVector();
                z = Direction.EAST.getVector();
                break;
            case EAST, WEST:
                x = Direction.NORTH.getVector();
                z = Direction.UP.getVector();
                break;
            default: //NORTH, SOUTH:
                x = Direction.UP.getVector();
                z = Direction.EAST.getVector();
        }
        Set<BlockPos> connectedBlocks = new HashSet<>();
        return getConnectedBlocksInSet(positions,origin,x,z,connectedBlocks);
    }

    private Set<BlockPos> getConnectedBlocksInSet(Set<BlockPos> positions, BlockPos origin, Vec3i x, Vec3i z,Set<BlockPos> connectedBlocks) {
        BlockPos target = origin.add(x);
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        target = origin.add(z);
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        target = origin.add(x.multiply(-1));
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        target = origin.add(z.multiply(-1));
        if(positions.contains(target)){
            connectedBlocks.add(target);
            positions.remove(target);
            connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
        }
        if(Spellbound.config.widened.IS_DIAGONAL_CONTIGUOUS){
            target = origin.add(x).add(z);
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
            target = origin.add(x).add(z.multiply(-1));
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
            target = origin.add(x.multiply(-1).add(z));
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
            target = origin.add(x.multiply(-1)).add(z.multiply(-1));
            if(positions.contains(target)){
                connectedBlocks.add(target);
                positions.remove(target);
                connectedBlocks = getConnectedBlocksInSet(positions,target,x,z,connectedBlocks);
            }
        }
        return connectedBlocks;
    }

    private void breakBlocksInWorld(Set<BlockPos> positions, World world, ItemStack tool, PlayerEntity player){
        for (BlockPos blockPos : positions) {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (blockState.isAir()) continue; //shouldn't be needed due to validation step, but not a bad idea even so.
            world.getProfiler().push("explosion_blocks");
            if (world instanceof ServerWorld) {
                BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
                Block.dropStacks(blockState,world,blockPos,blockEntity,player,tool);
            }
            world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            world.getProfiler().pop();
            SBEnchantmentHelper.onBreakBlock(block,world,blockPos,blockState,player);
        }
    }


    private void useWidenedArea(int level, ItemStack stack, ItemUsageContext context){
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return;
        }
        player.raycast(10,1,false);

        Vec3d cameraPos = player.getCameraPosVec(1);
        Vec3d rotation = player.getRotationVec(1);
        Vec3d combined = cameraPos.add(rotation.x * 10, rotation.y * 10, rotation.z * 10);

        BlockHitResult blockHitResult = context.getWorld().raycast(new RaycastContext(cameraPos, combined, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player));

        if(blockHitResult.getType() == HitResult.Type.BLOCK){
            Direction dir = blockHitResult.getSide();
            Set<BlockPos> positions = findBlocksInRange(context.getBlockPos(),dir,level);
            if(Spellbound.config.widened.REQUIRE_UNCOVERED_BLOCK){
                positions = getUncoveredBlocks(positions, context.getWorld(), dir);
            }
            if(Spellbound.config.widened.REQUIRE_CONTIGUOUS_BREAK){
                positions = getConnectedBlocksInSet(positions,context.getBlockPos(),dir);
            }
            useOnBlocksInWorld(positions,stack,context);
        }
    }

    private void useOnBlocksInWorld(Set<BlockPos> positions, ItemStack stack, ItemUsageContext context){
        for (BlockPos blockPos:
             positions) {
            PlayerEntity playerEntity = context.getPlayer();
            BlockPos blockOffset = blockPos.subtract(context.getBlockPos());
            ItemUsageContext newContext = new ItemUsageContext(context.getWorld(),context.getPlayer(),context.getHand(),stack,
                    new BlockHitResult(context.getHitPos().add(blockOffset.getX(),blockOffset.getY(),blockOffset.getZ()),
                            context.getSide(),blockPos,context.hitsInsideBlock()));
            CachedBlockPosition cachedBlockPosition = new CachedBlockPosition(newContext.getWorld(), blockPos, false);
            if (playerEntity != null && !playerEntity.getAbilities().allowModifyWorld && !stack.canPlaceOn(newContext.getWorld().getRegistryManager().get(RegistryKeys.BLOCK), cachedBlockPosition)) {
                return;
            }
            Item item = stack.getItem();

            if(Spellbound.DEBUG){
                Block outputBlock = context.getWorld().getBlockState(newContext.getBlockPos()).getBlock();
                Spellbound.LOGGER.info("Widened enchantment using "+stack.getName().getString()+" on block "+outputBlock+" at ");
                Spellbound.LOGGER.info("x = "+newContext.getBlockPos().getX()+" y = "+newContext.getBlockPos().getY()+" z = "+newContext.getBlockPos().getZ());
            }
            ActionResult actionResult = item.useOnBlock(newContext);
            if (playerEntity != null && actionResult.shouldIncrementStat()) {
                playerEntity.incrementStat(Stats.USED.getOrCreateStat(item));
            }
        }
    }
}
