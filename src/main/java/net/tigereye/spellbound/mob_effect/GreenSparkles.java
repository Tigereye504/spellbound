package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.world.explosion.Explosion;

public class GreenSparkles extends SBStatusEffect{

    public GreenSparkles(){
        super(StatusEffectType.NEUTRAL, 0x49be50);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {    }
}
