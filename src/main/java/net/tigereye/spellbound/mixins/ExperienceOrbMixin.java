package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrb.class)
public class ExperienceOrbMixin{
    @Shadow
    private int value;

    @Inject(at = @At("HEAD"), method = "playerTouch")
    public void spellboundOnPlayerCollisionMixin(Player player, CallbackInfo ci){
        if(!player.level().isClientSide()) {
            SBEnchantmentHelper.onGainExperience(player, value);
        }
    }
}
