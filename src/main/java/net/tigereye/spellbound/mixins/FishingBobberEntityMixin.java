package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public class FishingBobberEntityMixin {

    @Shadow @Nullable private Entity hookedEntity;

    @Inject( at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/FishingBobberEntity;pullHookedEntity(Lnet/minecraft/entity/Entity;)V"), method = "use")
    public void SpellboundFishingBobberEntityUseMixin(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        SBEnchantmentHelper.onPullHookedEntity(((FishingBobberEntity)(Object)this),usedItem,hookedEntity);
    }

}
