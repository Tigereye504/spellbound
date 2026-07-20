package net.tigereye.spellbound.mob_effect;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class Tethered extends SBStatusEffect implements CustomDataStatusEffect{
    public Tethered(){
        super(MobEffectCategory.HARMFUL, 0xaaaaaa);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        //Spellbound.LOGGER.info("Dragging Target");
        MobEffectInstance temp = entity.getEffect(SBStatusEffects.TETHERED);
        if(temp instanceof OwnedStatusEffectInstance ti){
            //attempt to get any missing data. Remove the debuff if it fails because we can't use it.
            if(!ti.fillMissingOwnerData(entity)){
                entity.removeEffect(SBStatusEffects.TETHERED);
                return;
            }
            //if the anchor has been removed from the world, remove the tether
            if(ti.owner.isRemoved()){
                entity.removeEffect(SBStatusEffects.TETHERED);
                return;
            }
            //otherwise, drag them in if they are past the leash
            entity.fallDistance = 0;

            Vec3 pullVector = new Vec3(ti.owner.getX() - entity.getX(), ti.owner.getY() - entity.getY(), ti.owner.getZ() - entity.getZ());

            if(!(entity.level().isClientSide)) {
                if (pullVector.length() >= Spellbound.config.tethering.LEASH_LENGTH) {
                    entity.setPosRaw(entity.getX(), entity.getY() + pullVector.y * 0.015D * Spellbound.config.tethering.ATTRACTION_FACTOR, entity.getZ());

                    double d = 0.05D * Spellbound.config.tethering.ATTRACTION_FACTOR;
                    Vec3 impulseVector = pullVector.scale(d).subtract(entity.getDeltaMovement().scale(0.15D));
                    entity.push(impulseVector.x, impulseVector.y, impulseVector.z);
                    entity.hurtMarked = true;
                }
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
    }

    @Override
    public MobEffectInstance getInstanceFromTag(CompoundTag tag) {
        return OwnedStatusEffectInstance.customFromNbt(this, tag);
    }
}
