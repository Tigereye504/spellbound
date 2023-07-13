package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.tigereye.spellbound.Spellbound;

public class Hovering extends SBStatusEffect{

    public Hovering(){
        super(StatusEffectCategory.BENEFICIAL, 0x194212);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        entity.setVelocity(entity.getVelocity().getX(), Spellbound.config.hover.UPWARD_DRIFT,entity.getVelocity().getZ());
        entity.fallDistance = 0;
    }
}
