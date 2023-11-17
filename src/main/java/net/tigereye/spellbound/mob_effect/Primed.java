package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.util.SpellboundUtil;

public class Primed extends SBStatusEffect{

    public Primed(){
        super(StatusEffectCategory.NEUTRAL, 0x194212);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration <= 1;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            SpellboundUtil.psudeoExplosion(entity, false, entity.getPos()
                    , ((amplifier*amplifier)+1)
                    ,(amplifier+1)*2
                    ,(amplifier+1)*Spellbound.config.outburst.SHOCKWAVE_FORCE);
        }
    }
}
