package net.tigereye.spellbound.mixins;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
public class ProjectileEntityMixin {
    @Inject(at = @At(value = "TAIL"), method = "onBlockHit")
    protected void spellboundPersistentProjectileEntityOnBlockHitMixin(BlockHitResult blockHitResult, CallbackInfo info){
        SBEnchantmentHelper.onProjectileBlockHit((ProjectileEntity)(Object)this, blockHitResult);
    }
}
