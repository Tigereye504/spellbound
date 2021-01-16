package net.tigereye.spellbound.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 0),ordinal = 0, method = "onEntityHit")
    protected int spellboundPersistentProjectileEntityOnEntityHitMixin(int damage, EntityHitResult entityHitResult){
        return SBEnchantmentHelper.getProjectileDamage((PersistentProjectileEntity)(Object)this, entityHitResult, damage);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/Entity;)V"), method = "onEntityHit")
    public void spellboundPersistentProjectileEntityOnEntityHitMixinTwo(EntityHitResult entityHitResult, CallbackInfo info){
        SBEnchantmentHelper.onProjectileEntityHit((PersistentProjectileEntity)(Object)this, entityHitResult.getEntity());
    }
}