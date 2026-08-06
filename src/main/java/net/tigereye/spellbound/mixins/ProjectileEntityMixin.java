package net.tigereye.spellbound.mixins;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.tigereye.spellbound.interfaces.SpellboundProjectileEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Projectile.class)
public class ProjectileEntityMixin  implements SpellboundProjectileEntity {
    private ItemStack source = null;

    @Override
    public ItemStack getSource() {
        return source;
    }

    @Override
    public void setSource(ItemStack source) {
        this.source = source;
    }

    @Inject(at = @At(value = "HEAD"), method = "setOwner")
    protected void spellboundPersistentProjectileEntitySetOwnerMixin(Entity owner, CallbackInfo info){
        if(owner != null){
            if(owner instanceof LivingEntity){
                InteractionHand hand = ((LivingEntity) owner).getUsedItemHand();
                if(hand != null) {
                    setSource(((LivingEntity) owner).getItemInHand(hand));
                    SBEnchantmentHelper.onFireProjectile(owner,getSource(),(Projectile)(Object)this);
                }
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "onHitBlock")
    protected void spellboundPersistentProjectileEntityOnBlockHitMixin(BlockHitResult blockHitResult, CallbackInfo info){
        SBEnchantmentHelper.onProjectileBlockHit((Projectile)(Object)this, blockHitResult);
    }
}
