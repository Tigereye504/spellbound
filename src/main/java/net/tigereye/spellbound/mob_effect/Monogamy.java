package net.tigereye.spellbound.mob_effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.util.SpellboundUtil;

public class Monogamy extends SBStatusEffect implements CustomDataStatusEffect{
    public Monogamy(){
        super(MobEffectCategory.NEUTRAL, 0xaaaaaa);
    }


    public boolean isDurationEffectTick(int duration, int amplifier) {
        if(Spellbound.config.YANDERE_TOOLS){
            return duration % 160 == 0;
        }
        return false;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if(Spellbound.config.YANDERE_TOOLS){
            SpellboundUtil.YandereViolence(entity);
        }
    }

    @Override
    public MobEffectInstance getInstanceFromTag(CompoundTag tag) {
        return MonogamyInstance.customFromNbt(tag);
    }

}
