package net.tigereye.spellbound.util;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;

import java.util.List;

public class SpellboundUtil {

    public static void pushPullEntitiesPlayersInRange(double range, double strength, LivingEntity user){
        Vec3 position = user.position();
        List<Entity> entityList = user.level().getEntitiesOfClass(Entity.class,
                new AABB(position.x+ range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (Entity target:
                entityList) {
            if(target != user && (target instanceof LivingEntity || target instanceof ItemEntity)
                    //&& !(target instanceof PlayerEntity)
                    && !(user.hasIndirectPassenger(target) || target.hasIndirectPassenger(user))
                    && !(target.isPassenger())
            ) {
                Vec3 forceVec = position.subtract(target.position()).normalize();
                if (target instanceof LivingEntity) {
                    forceVec = forceVec.scale(strength * Math.max(0, 1 - ((LivingEntity) target).getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
                } else {
                    forceVec = forceVec.scale(strength);
                }
                target.push(forceVec.x, target.onGround() ? 0 : forceVec.y, forceVec.z);
                target.hasImpulse = true;
            }
        }
        /*
        List<PlayerEntity> playerList = user.getWorld().getPlayers(TargetPredicate.DEFAULT,user,
                new Box(position.x+range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (LivingEntity target:
                playerList) {
            if(target != user) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                forceVec = forceVec.multiply(strength * Math.max(0, 1 - (target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                target.velocityModified = true;
            }
        }
        */
    }

    public static boolean isPositionObstructed(Level world, BlockPos pos){
        boolean feetBlocked = world.getBlockState(pos).canOcclude();
        boolean headBlocked = world.getBlockState(pos.above()).canOcclude();
        return feetBlocked || headBlocked;
    }

    public static void psudeoExplosion(Entity source, boolean excludeSource, Vec3 position, float strength, float radius, float force, float fullDamageRadius){
        if(radius == 0){
            return;
        }
        List<LivingEntity> entityList = source.level().getEntitiesOfClass(LivingEntity.class,
                new AABB(position.x+ radius,position.y+radius,position.z+radius,
                        position.x-radius,position.y-radius,position.z-radius));
        for (LivingEntity target:
                entityList) {
            if(target != source || !excludeSource) {
                Vec3 forceVec = target.position().subtract(position);
                float distance = (float) forceVec.length();
                if(distance < radius) {
                    float proximityRatio = 1;
                    if (distance > fullDamageRadius){
                        proximityRatio = 1f - ((distance - fullDamageRadius) / (radius-fullDamageRadius));
                    }
                    float exposure = Explosion.getSeenPercent(position,target);
                    target.hurt(source.damageSources().source(DamageTypes.EXPLOSION,source), strength * proximityRatio * exposure);

                    forceVec = forceVec.multiply(1,0,1).normalize().add(0,.1,0);
                    forceVec = forceVec.scale(force * proximityRatio * exposure * Math.max(0, 1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));

                    target.push(forceVec.x, forceVec.y, forceVec.z);
                    target.hurtMarked = true;
                }
            }
        }

        if(Spellbound.config.DESTRUCTIVE_SHOCKWAVES && source.level() instanceof ServerLevel){
            int blockRange = Math.round(radius)+2;
            float squaredRange = radius*radius;
            float squaredStrength = strength*strength;
            BlockPos lowerCorner = source.blockPosition().offset(1-blockRange,1-blockRange,1-blockRange);
            int size = (blockRange*2)-1;
            Level world = source.level();
            BlockPos target;
            BlockState targetBlock;
            Block block;
            ObjectArrayList<Pair<ItemStack, BlockPos>> objectArrayList = new ObjectArrayList<>();
            Explosion dummyExplosion = new Explosion(world,source, source.getX(), source.getY(), source.getZ(), 0.1f,false, Explosion.BlockInteraction.DESTROY_WITH_DECAY);
            for(int y = 0; y < size; y++){
                if(lowerCorner.getY()+y >= 0){
                    for(int x = 0; x < size; x++){
                        for(int z = 0; z < size; z++){
                            target = lowerCorner.offset(x,y,z);
                            double distanceFromEdge = radius-Math.sqrt(target.distSqr(source.blockPosition()));
                            double squaredDistanceFromEdge = distanceFromEdge*distanceFromEdge;
                            targetBlock = world.getBlockState(target);
                            block = targetBlock.getBlock();
                            if(!targetBlock.isAir() &&
                                    distanceFromEdge > 0 &&
                                    targetBlock.getBlock().getExplosionResistance() <= squaredStrength*squaredDistanceFromEdge/squaredRange)
                            {
                                BlockPos blockPos2 = target.immutable();
                                world.getProfiler().push("explosion_blocks");
                                if (block.dropFromExplosion(dummyExplosion) && world instanceof ServerLevel) {
                                    BlockEntity blockEntity = targetBlock.hasBlockEntity() ? world.getBlockEntity(target) : null;
                                    LootParams.Builder builder = (new LootParams.Builder((ServerLevel)world))
                                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(target))
                                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                                            .withOptionalParameter(LootContextParams.THIS_ENTITY, source)
                                            .withParameter(LootContextParams.EXPLOSION_RADIUS, strength/radius);
                                    targetBlock.getDrops(builder).forEach((stack) -> tryMergeStack(objectArrayList, stack, blockPos2));
                                }

                                world.setBlock(target, Blocks.AIR.defaultBlockState(), 3);
                                block.wasExploded(world, target, dummyExplosion);
                                world.getProfiler().pop();
                            }
                        }
                    }
                }
            }

            for (Pair<ItemStack, BlockPos> itemStackBlockPosPair : objectArrayList) {
                Block.popResource(world, itemStackBlockPosPair.getSecond(), itemStackBlockPosPair.getFirst());
            }
        }

        //draw explosion particles

        if (strength < 4.0f) {
            source.level().addParticle(ParticleTypes.EXPLOSION, position.x, position.y, position.z, 1.0, 0.0, 0.0);
        } else {
            source.level().addParticle(ParticleTypes.EXPLOSION_EMITTER, position.x, position.y, position.z, 1.0, 0.0, 0.0);
        }

        source.getCommandSenderWorld().playSound(null, position.x(), position.y(), position.z(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS,
                (float) Math.min(4,Math.sqrt(strength)), (1.0F + (source.level().random.nextFloat() - source.level().random.nextFloat()) * 0.2F) * 0.7F);
    }

    public static void psudeoExplosion(Entity source, boolean excludeSource, Vec3 position, float strength, float range, float force){
        psudeoExplosion(source, excludeSource, position, strength, range, force, 0);
    }

    private static void tryMergeStack(ObjectArrayList<Pair<ItemStack, BlockPos>> stacks, ItemStack stack, BlockPos pos) {
        int i = stacks.size();

        for(int j = 0; j < i; ++j) {
            Pair<ItemStack, BlockPos> pair = stacks.get(j);
            ItemStack itemStack = pair.getFirst();
            if (ItemEntity.areMergable(itemStack, stack)) {
                ItemStack itemStack2 = ItemEntity.merge(itemStack, stack, 16);
                stacks.set(j, Pair.of(itemStack2, pair.getSecond()));
                if (stack.isEmpty()) {
                    return;
                }
            }
        }

        stacks.add(Pair.of(stack, pos));
    }

    public static Enchantment.Rarity rarityLookup(int configValue){
        return switch (configValue) {
            case 1 -> Enchantment.Rarity.COMMON;
            case 2 -> Enchantment.Rarity.UNCOMMON;
            case 3 -> Enchantment.Rarity.RARE;
            default -> Enchantment.Rarity.VERY_RARE;
        };
    }

    public static void ReplaceAttributeModifier(AttributeInstance att, AttributeModifier mod)
    {
        //removes any existing mod and replaces it with the updated one.
        att.removeModifier(mod.getId());
        att.addPermanentModifier(mod);
    }
}
