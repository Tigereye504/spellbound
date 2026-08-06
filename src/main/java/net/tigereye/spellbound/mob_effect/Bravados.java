package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

public class Bravados extends SBStatusEffect{

    public Bravados(){
        super(MobEffectCategory.NEUTRAL, 0x194212);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration == 1;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        entity.setAbsorptionAmount(0);
        SBEnchantmentHelper.onTakeRedHealthDamage(entity.damageSources().generic(),entity,0);
        return true;
    }
}
