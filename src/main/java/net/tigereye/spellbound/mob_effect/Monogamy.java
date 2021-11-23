package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;

public class Monogamy extends SBStatusEffect implements CustomDataStatusEffect{
    public Monogamy(){
        super(StatusEffectCategory.NEUTRAL, 0xaaaaaa);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {}

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return MonogamyInstance.customFromNbt(tag);
    }

}
