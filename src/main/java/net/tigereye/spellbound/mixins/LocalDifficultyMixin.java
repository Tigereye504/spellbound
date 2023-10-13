package net.tigereye.spellbound.mixins;

import net.minecraft.world.LocalDifficulty;
import net.tigereye.spellbound.interfaces.SpellboundLocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalDifficulty.class)
public class LocalDifficultyMixin implements SpellboundLocalDifficulty {
    @Unique
    private float spellboundLocalDifficultyModifier = 0;

    @Inject(at = @At("RETURN"), method = "getLocalDifficulty", cancellable = true)
    public void spellboundGetLocalDifficultyMixin(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(cir.getReturnValue()+spellboundLocalDifficultyModifier);
    }

    @Inject(at = @At("RETURN"), method = "getClampedLocalDifficulty", cancellable = true)
    public void spellboundGetClampedLocalDifficultyMixin(CallbackInfoReturnable<Float> cir){
        cir.setReturnValue(cir.getReturnValue()+(spellboundLocalDifficultyModifier/2));
    }

    @Override
    public void spellbound$setLocalDifficultyModifier(float modifier) {
        spellboundLocalDifficultyModifier = modifier;
    }
}
