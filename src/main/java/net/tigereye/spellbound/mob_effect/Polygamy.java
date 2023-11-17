package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.PolygamyInstance;
import net.tigereye.spellbound.util.SpellboundUtil;

public class Polygamy extends SBStatusEffect implements CustomDataStatusEffect{
    public Polygamy(){
        super(StatusEffectCategory.NEUTRAL, 0xbbbbbb);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if(Spellbound.config.YANDERE_TOOLS){
            return duration % 160 == 0;
        }
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (Spellbound.config.YANDERE_TOOLS) {
            SpellboundUtil.YandereViolence(entity);
        }
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return PolygamyInstance.customFromNbt(tag);
    }
}
