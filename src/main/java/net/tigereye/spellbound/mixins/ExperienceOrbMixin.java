package net.tigereye.spellbound.mixins;

import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public class ExperienceOrbMixin{
    @Shadow
    private int amount;

    @Inject(at = @At("HEAD"), method = "onPlayerCollision")
    public void spellboundOnPlayerCollisionMixin(PlayerEntity player, CallbackInfo ci){
        if(!player.getWorld().isClient()) {
            SBEnchantmentHelper.onGainExperience(player, amount);
        }
    }
}
