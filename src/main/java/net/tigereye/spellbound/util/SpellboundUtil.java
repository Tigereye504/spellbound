package net.tigereye.spellbound.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.AbstractDecorationEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;

import java.util.List;

public class SpellboundUtil {

    public static void pushPullEntitiesPlayersInRange(double range, double strength, LivingEntity user){
        Vec3d position = user.getPos();
        List<Entity> entityList = user.world.getNonSpectatingEntities(Entity.class,
                new Box(position.x+ range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (Entity target:
                entityList) {
            if(target != user && !(target instanceof AbstractDecorationEntity)) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                if (target instanceof LivingEntity) {
                    forceVec = forceVec.multiply(strength * Math.max(0, 1 - ((LivingEntity) target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                } else {
                    forceVec = forceVec.multiply(strength);
                }
                target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                target.velocityModified = true;
            }
        }
        List<PlayerEntity> playerList = user.world.getPlayers(TargetPredicate.DEFAULT,user,
                new Box(position.x+range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (Entity target:
                playerList) {
            if(target != user) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                if (target instanceof LivingEntity) {
                    forceVec = forceVec.multiply(strength * Math.max(0, 1 - ((LivingEntity) target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                } else {
                    forceVec = forceVec.multiply(strength);
                }
                target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                target.velocityModified = true;
            }
        }
    }

    public static boolean isPositionObstructed(World world, BlockPos pos){
        boolean feetBlocked = world.getBlockState(pos).isOpaque();
        boolean headBlocked = world.getBlockState(pos.up()).isOpaque();
        return feetBlocked || headBlocked;
    }

    public static void setMotionTracker(ItemStack stack, LivingEntity entity){
        long time = entity.world.getTime();
        NbtCompound tag = stack.getOrCreateSubNbt(Spellbound.MODID+"MotionTracker");
        long previousTime = tag.getLong("pretime");
        if(previousTime == time){
            return;
        }
        tag.putDouble("x",tag.getDouble("preX"));
        tag.putDouble("y",tag.getDouble("preY"));
        tag.putDouble("z",tag.getDouble("preZ"));
        tag.putLong("time",previousTime);
        tag.putDouble("preX",entity.getX());
        tag.putDouble("preY",entity.getY());
        tag.putDouble("preZ",entity.getZ());
        tag.putLong("pretime",time);
    }

    public static Vec3d readMotionTracker(LivingEntity entity, ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(Spellbound.MODID+"MotionTracker");
        double x = tag.getDouble("x");
        double y = tag.getDouble("y");
        double z = tag.getDouble("z");
        long time = tag.getLong("time");
        Vec3d velocityRead = new Vec3d((entity.getX()-x)/(Math.max(entity.world.getTime()-time,1)),(entity.getY()-y)/(Math.max(entity.world.getTime()-time,1)),(entity.getZ()-z)/(Math.max(entity.world.getTime()-time,1)));
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Read velocity x:" + velocityRead.x + " y:" + velocityRead.y + " z:" + velocityRead.z);
        }
        return velocityRead;
    }

    public static void psudeoExplosion(LivingEntity source, boolean excludeSource, Vec3d position, float strength, float range, float force){
        List<LivingEntity> entityList = source.world.getNonSpectatingEntities(LivingEntity.class,
                new Box(position.x+ range,position.y+range,position.z+range,
                        position.x-range,position.y-range,position.z-range));
        for (LivingEntity target:
                entityList) {
            if(target != source || !excludeSource) {
                Vec3d forceVec = target.getPos().subtract(position);
                float distance = (float) forceVec.length();
                if(distance < range) {
                    float proximityRatio = (range-distance) / range;
                    target.damage(DamageSource.explosion(source), strength * proximityRatio);

                    forceVec = forceVec.multiply(1,0,1).add(0,.1,0).normalize();
                    forceVec = forceVec.multiply(force * proximityRatio * Math.max(0, 1 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));

                    target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                    target.velocityModified = true;
                }
            }
        }

        source.getEntityWorld().playSound(null, position.getX(), position.getY(), position.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (source.world.random.nextFloat() - source.world.random.nextFloat()) * 0.2F) * 0.7F);
    }
}
