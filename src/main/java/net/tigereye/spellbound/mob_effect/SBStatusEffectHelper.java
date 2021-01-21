package net.tigereye.spellbound.mob_effect;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class SBStatusEffectHelper {



    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBStatusEffectHelper.forEachStatusEffect((instance) -> {
            if(instance.getEffectType() instanceof SBStatusEffect) {
                mutableFloat.setValue(((SBStatusEffect)(instance.getEffectType())).onPreArmorDefense(instance, source, defender, mutableFloat.floatValue()));
            }
        }, defender.getStatusEffects());
        return mutableFloat.floatValue();
    }

    private static void forEachStatusEffect(SBStatusEffectHelper.Consumer consumer, Collection<StatusEffectInstance> effects) {
        for (StatusEffectInstance effect:
             effects) {
            consumer.accept(effect);
        }
    }

    @FunctionalInterface
    interface Consumer {
        void accept(StatusEffectInstance instance);
    }
}
