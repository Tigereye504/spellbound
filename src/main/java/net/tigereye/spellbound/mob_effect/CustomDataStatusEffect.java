package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;

public interface CustomDataStatusEffect {
    StatusEffectInstance getInstanceFromTag(NbtCompound tag);
}
