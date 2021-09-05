package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class Tethered extends SBStatusEffect implements CustomDataStatusEffect{
    public Tethered(){
        super(StatusEffectType.HARMFUL, 0xaaaaaa);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        //Spellbound.LOGGER.info("Dragging Target");
        if(!(entity.world.isClient)){
            StatusEffectInstance temp = entity.getStatusEffect(SBStatusEffects.TETHERED);
            if(temp instanceof TetheredInstance){
                TetheredInstance ti = (TetheredInstance)temp;
                //attempt to get any missing data. Remove the debuff if if fails because we can't use it.
                if(!fillMissingTetheredData(ti,entity)){
                    entity.removeStatusEffect(SBStatusEffects.TETHERED);
                    return;
                }
                //if the anchor has been removed from the world, remove the tether
                if(ti.anchor.isRemoved()){
                    entity.removeStatusEffect(SBStatusEffects.TETHERED);
                    return;
                }
                //otherwise, drag them in
                Vec3d vec3d = new Vec3d(ti.anchor.getX() - entity.getX(), ti.anchor.getY() - entity.getY(), ti.anchor.getZ() - entity.getZ());
                entity.setPos(entity.getX(), entity.getY() + vec3d.y * 0.015D * Spellbound.config.TETHER_ATTRACTION_FACTOR, entity.getZ());

                double d = 0.05D * Spellbound.config.TETHER_ATTRACTION_FACTOR;
                entity.setVelocity(entity.getVelocity().multiply(0.95D).add(vec3d.multiply(d)));
            }
        }
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return TetheredInstance.customFromNbt(tag);
    }

    public boolean fillMissingTetheredData(TetheredInstance tetheredInstance, LivingEntity entity){
        if(tetheredInstance.anchor == null){
            if(tetheredInstance.tetherUUID == null) {
                return false;
            }
            else{
                ServerWorld world;
                if(entity.world instanceof ServerWorld){
                    world = (ServerWorld)entity.world;
                }
                else{
                    return false;
                }
                tetheredInstance.anchor = world.getEntity(tetheredInstance.tetherUUID);
                if(tetheredInstance.anchor == null) {return false;}
            }
        }
        if(tetheredInstance.tetherUUID == null){
            tetheredInstance.tetherUUID = tetheredInstance.anchor.getUuid();
        }
        return true;
    }
}
