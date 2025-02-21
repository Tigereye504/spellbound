package net.tigereye.spellbound.mixins.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityClientMixin {

    @Shadow public abstract World getWorld();

    @Inject(at = @At("RETURN"), method = "isGlowing", cancellable = true)
    private void SpellboundEntityClientIsGlowingMixin(CallbackInfoReturnable<Boolean> cir) {
        if(this.getWorld().isClient()){
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player != null) {
                cir.setReturnValue(SBEnchantmentHelper.onClientEntityIsGlowing(player,(Entity)(Object)this,cir.getReturnValue()));
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "getTeamColorValue", cancellable = true)
    private void SpellboundEntityClientGetTeamColorValueMixin(CallbackInfoReturnable<Integer> cir) {
        if(this.getWorld().isClient()){
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player != null) {
                cir.setReturnValue(SBEnchantmentHelper.overwriteClientEntityTeamColor(player,(Entity)(Object)this,cir.getReturnValue()));
            }
        }
    }
}
