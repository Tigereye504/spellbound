package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.mob_effect.instance.PolygamyInstance;

public class Polygamy extends SBStatusEffect implements CustomDataStatusEffect{
    public Polygamy(){
        super(StatusEffectType.NEUTRAL, 0xbbbbbb);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return PolygamyInstance.customFromNbt(tag);
    }
}
