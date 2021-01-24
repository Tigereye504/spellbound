package net.tigereye.spellbound.mob_effect;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectType;
import net.minecraft.item.ItemStack;

import java.util.List;

public class SBStatusEffect extends StatusEffect {

    public SBStatusEffect(StatusEffectType type, int color) {
        super(type, color);
    }

    //for when the user is struck, before armor is applied
    public float onPreArmorDefense(StatusEffectInstance instance, DamageSource source, LivingEntity defender, float amount, List<StatusEffectInstance> effectsToAdd, List<StatusEffect> effectsToRemove){return amount;}
}