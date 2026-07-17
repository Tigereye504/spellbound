package net.tigereye.spellbound.mixins.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityClientMixin {

    @Shadow public abstract Level level();

    @Inject(at = @At("RETURN"), method = "isCurrentlyGlowing", cancellable = true)
    private void SpellboundEntityClientIsGlowingMixin(CallbackInfoReturnable<Boolean> cir) {
        if(this.level().isClientSide()){
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null) {
                cir.setReturnValue(SBEnchantmentHelper.onClientEntityIsGlowing(player,(Entity)(Object)this,cir.getReturnValue()));
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "getTeamColor", cancellable = true)
    private void SpellboundEntityClientGetTeamColorValueMixin(CallbackInfoReturnable<Integer> cir) {
        if(this.level().isClientSide()){
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null) {
                cir.setReturnValue(SBEnchantmentHelper.overwriteClientEntityTeamColor(player,(Entity)(Object)this,cir.getReturnValue()));
            }
        }
    }
}
