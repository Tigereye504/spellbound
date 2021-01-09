package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class Monogamy extends SBStatusEffect implements CustomDataStatusEffect{
    public Monogamy(){
        super(StatusEffectType.NEUTRAL, 0xaaaaaa);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    @Override
    public StatusEffectInstance getInstanceFromTag(CompoundTag tag) {
        return MonogamyInstance.customFromTag(tag);
    }

}
