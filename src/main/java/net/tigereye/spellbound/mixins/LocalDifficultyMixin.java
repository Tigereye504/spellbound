package net.tigereye.spellbound.mixins;

import net.minecraft.world.DifficultyInstance;
import net.tigereye.spellbound.interfaces.SpellboundLocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyInstance.class)
public class LocalDifficultyMixin implements SpellboundLocalDifficulty {
    @Unique
    private float spellboundLocalDifficultyModifier = 0;

    @Inject(at = @At("RETURN"), method = "getEffectiveDifficulty", cancellable = true)
    public void spellboundGetLocalDifficultyMixin(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(cir.getReturnValue()+spellboundLocalDifficultyModifier);
    }

    @Inject(at = @At("RETURN"), method = "getSpecialMultiplier", cancellable = true)
    public void spellboundGetClampedLocalDifficultyMixin(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(cir.getReturnValue()+(spellboundLocalDifficultyModifier/2));
    }

    @Override
    public void spellbound$setLocalDifficultyModifier(float modifier) {
        spellboundLocalDifficultyModifier = modifier;
    }
}
