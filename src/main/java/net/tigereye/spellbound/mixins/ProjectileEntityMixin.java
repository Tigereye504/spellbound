package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.SpellboundProjectileEntity;
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
                    source = ((LivingEntity) owner).getStackInHand(hand);
                }
            }
            else {
                for (ItemStack stack : owner.getItemsHand()) {
                    if (stack.getItem() instanceof RangedWeaponItem) {
                        setSource(stack);
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
