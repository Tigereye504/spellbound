package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.GameRules;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.Optional;

public class Primed extends SBStatusEffect{

    public Primed(){
        super(StatusEffectCategory.NEUTRAL, 0x194212);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.world.isClient)){
            SpellboundUtil.psudeoExplosion(entity, false, entity.getPos()
                    , ((amplifier*amplifier)+1)
                    ,(amplifier+1)*2
                    ,(amplifier+1)*Spellbound.config.outburst.SHOCKWAVE_FORCE);
        }
    }
}
