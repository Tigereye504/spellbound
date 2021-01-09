package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.CompoundTag;

public interface CustomDataStatusEffect {
    StatusEffectInstance getInstanceFromTag(CompoundTag tag);
}
