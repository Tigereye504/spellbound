package net.tigereye.spellbound.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.tigereye.spellbound.mob_effect.CustomDataStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEffectInstance.class)
public class StatusEffectInstanceMixin {
    // Lnet/minecraft/entity/effect/StatusEffectInstance;fromNbt(
    //  Lnet/minecraft/entity/effect/StatusEffect;
    //  Lnet/minecraft/nbt/NbtCompound;
    // )Lnet/minecraft/entity/effect/StatusEffectInstance;
    @Inject(at = @At("HEAD"), method = "loadSpecifiedEffect", cancellable = true)
    private static void spellboundStatusEffectInstanceFromTagMixin(MobEffect type, CompoundTag tag, CallbackInfoReturnable<MobEffectInstance> info){
        if(type instanceof CustomDataStatusEffect){
            info.setReturnValue(((CustomDataStatusEffect)type).getInstanceFromTag(tag));
            info.cancel();
        }
    }
}
