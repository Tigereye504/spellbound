package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;

import java.util.Optional;

public class Primed extends SBStatusEffect{

    public Primed(){
        super(StatusEffectType.NEUTRAL, 0x194212);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.world.isClient)){
            entity.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE,1,3,false,false,false));
            entity.world.createExplosion(null, entity.getX(), entity.getY()+.5, entity.getZ(), amplifier+1, Explosion.DestructionType.NONE);
        }
    }
}
