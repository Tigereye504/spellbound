package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.tigereye.spellbound.interfaces.SpellboundProjectileEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
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
                Hand hand = ((LivingEntity) owner).getActiveHand();
                if(hand != null) {
                    setSource(((LivingEntity) owner).getStackInHand(hand));
                    SBEnchantmentHelper.onFireProjectile(owner,getSource(),(ProjectileEntity)(Object)this);
                }
            }
            else {
                for (ItemStack stack : owner.getHandItems()) {
                    if (stack.getItem() instanceof RangedWeaponItem) {
                        setSource(stack);
                        SBEnchantmentHelper.onFireProjectile(owner,getSource(),(ProjectileEntity)(Object)this);
                    }
                }
            }
        }
    }

    @Inject(at = @At(value = "TAIL"), method = "onBlockHit")
    protected void spellboundPersistentProjectileEntityOnBlockHitMixin(BlockHitResult blockHitResult, CallbackInfo info){
        SBEnchantmentHelper.onProjectileBlockHit((ProjectileEntity)(Object)this, blockHitResult);
    }
}
