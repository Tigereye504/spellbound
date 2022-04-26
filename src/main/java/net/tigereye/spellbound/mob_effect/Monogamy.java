package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.registration.SBDamageSource;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class Monogamy extends SBStatusEffect implements CustomDataStatusEffect{
    public Monogamy(){
        super(StatusEffectCategory.NEUTRAL, 0xaaaaaa);
    }


    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        if(Spellbound.config.YANDERE_TOOLS){
            return duration % 160 == 0;
        }
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(Spellbound.config.YANDERE_TOOLS){
            SpellboundUtil.YandereViolence(entity);
        }
    }

    @Override
    public StatusEffectInstance getInstanceFromTag(NbtCompound tag) {
        return MonogamyInstance.customFromNbt(tag);
    }

}
