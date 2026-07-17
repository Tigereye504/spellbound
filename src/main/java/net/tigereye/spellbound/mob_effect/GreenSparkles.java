package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class GreenSparkles extends SBStatusEffect{

    public GreenSparkles(){
        super(MobEffectCategory.NEUTRAL, 0x49be50);
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return false;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {    }
}
