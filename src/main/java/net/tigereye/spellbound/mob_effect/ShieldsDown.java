package net.tigereye.spellbound.mob_effect;

import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class ShieldsDown extends SBStatusEffect{

    public ShieldsDown(){
        super(MobEffectCategory.NEUTRAL, 0x375159);
    } //7CB5C6 for shields up


    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(!(entity.level().isClientSide)){
            entity.addEffect(new MobEffectInstance(SBStatusEffects.SHIELDED,
                        MobEffectInstance.INFINITE_DURATION,
                        amplifier, false, false, false));
        }
    }
}
