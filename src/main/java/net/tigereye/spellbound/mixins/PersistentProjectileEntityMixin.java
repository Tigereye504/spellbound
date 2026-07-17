package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class PersistentProjectileEntityMixin extends ProjectileEntityMixin{
    @ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0),ordinal = 0, method = "onHitEntity")
    protected int spellboundPersistentProjectileEntityOnEntityHitMixin(int damage, EntityHitResult entityHitResult){
        return SBEnchantmentHelper.getProjectileDamage((AbstractArrow)(Object)this, entityHitResult, damage);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"), method = "onHitEntity")
    public void spellboundPersistentProjectileEntityOnEntityHitMixinTwo(EntityHitResult entityHitResult, CallbackInfo info){
        SBEnchantmentHelper.onProjectileEntityHit((AbstractArrow)(Object)this, entityHitResult.getEntity());
    }
}
