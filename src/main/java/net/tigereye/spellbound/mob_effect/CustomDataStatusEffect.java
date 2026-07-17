package net.tigereye.spellbound.mob_effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;

public interface CustomDataStatusEffect {
    MobEffectInstance getInstanceFromTag(CompoundTag tag);
}
