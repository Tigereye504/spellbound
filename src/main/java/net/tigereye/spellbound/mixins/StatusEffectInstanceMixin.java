package net.tigereye.spellbound.mixins;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.mob_effect.CustomDataStatusEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public class StatusEffectInstanceMixin {
    // Lnet/minecraft/entity/effect/StatusEffectInstance;fromNbt(
    //  Lnet/minecraft/entity/effect/StatusEffect;
    //  Lnet/minecraft/nbt/NbtCompound;
    // )Lnet/minecraft/entity/effect/StatusEffectInstance;
    @Inject(at = @At("HEAD"), method = "fromNbt(Lnet/minecraft/entity/effect/StatusEffect;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/effect/StatusEffectInstance;", cancellable = true)
    private static void spellboundStatusEffectInstanceFromTagMixin(StatusEffect type, NbtCompound tag, CallbackInfoReturnable<StatusEffectInstance> info){
        if(type instanceof CustomDataStatusEffect){
            info.setReturnValue(((CustomDataStatusEffect)type).getInstanceFromTag(tag));
            info.cancel();
        }
    }
}
