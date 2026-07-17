package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingHook.class)
public class FishingBobberEntityMixin {

    @Shadow @Nullable private Entity hookedIn;

    @Inject( at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/FishingHook;pullEntity(Lnet/minecraft/world/entity/Entity;)V"), method = "retrieve")
    public void SpellboundFishingBobberEntityUseMixin(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        SBEnchantmentHelper.onPullHookedEntity(((FishingHook)(Object)this),usedItem,hookedIn);
    }

}
