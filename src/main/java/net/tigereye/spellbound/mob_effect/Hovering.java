package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;

public class Hovering extends SBStatusEffect{

    public Hovering(){
        super(MobEffectCategory.BENEFICIAL, 0x194212);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setDeltaMovement(entity.getDeltaMovement().x(), Spellbound.config.hover.UPWARD_DRIFT,entity.getDeltaMovement().z());
        entity.fallDistance = 0;
    }
}
