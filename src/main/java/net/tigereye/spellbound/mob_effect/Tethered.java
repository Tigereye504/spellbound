package net.tigereye.spellbound.mob_effect;

import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;

public class Tethered extends SBStatusEffect{

    public static final String OWNER_KEY = Spellbound.MODID+"TetheredOwner";

    public Tethered(){
        super(MobEffectCategory.HARMFUL, 0xaaaaaa);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.fallDistance = 0;
        if(entity.level() instanceof ServerLevel sLevel) {
            UUID ownerUuid = ((SpellboundLivingEntity) entity).spellbound$getLastPrimer();
            Entity owner = sLevel.getEntity(ownerUuid);
            //if the anchor has been removed from the world, remove the tether
            if(owner.isRemoved()){
                return false;
            }
            //otherwise, drag them in if they are past the leash
            Vec3 pullVector = new Vec3(owner.getX() - entity.getX(), owner.getY() - entity.getY(), owner.getZ() - entity.getZ());
            if (pullVector.length() >= Spellbound.config.tethering.LEASH_LENGTH) {
                entity.setPosRaw(entity.getX(), entity.getY() + pullVector.y * 0.015D * Spellbound.config.tethering.ATTRACTION_FACTOR, entity.getZ());

                double d = 0.05D * Spellbound.config.tethering.ATTRACTION_FACTOR;
                Vec3 impulseVector = pullVector.scale(d).subtract(entity.getDeltaMovement().scale(0.15D));
                entity.push(impulseVector.x, impulseVector.y, impulseVector.z);
                entity.hurtMarked = true;
            }

            //draw particles between entity and anchor
            int sparks = entity.getRandom().nextInt(Math.min(20,Math.max(3,(int)(pullVector.lengthSqr()/5))));
            Vec3 basePosition = entity.position();
            for (int i = 0; i < sparks; i++) {
                Vec3 modifiedPosition = basePosition.add(pullVector.scale(entity.getRandom().nextFloat()));
                float driftX = (entity.getRandom().nextFloat() - .5f) * .15f;
                float driftY = (entity.getRandom().nextFloat() - .5f) * .15f;
                float driftZ = (entity.getRandom().nextFloat() - .5f) * .15f;
                entity.level().addParticle(ParticleTypes.ELECTRIC_SPARK,
                        modifiedPosition.x+driftX, modifiedPosition.y+driftY, modifiedPosition.z+driftZ,
                        0, 0, 0);
            }
        }
        return true;
    }
}
