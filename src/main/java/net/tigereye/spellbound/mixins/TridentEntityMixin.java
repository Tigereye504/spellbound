package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.EntityHitResult;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public class TridentEntityMixin {

    @Shadow
    protected ItemStack asItemStack() {
        return null;
    }

    // Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(
    //  Lnet/minecraft/item/ItemStack;
    //  Lnet/minecraft/entity/EntityGroup;
    // )F
    //
    //Lnet/minecraft/entity/projectile/TridentEntity;getOwner(
    //)Lnet/minecraft/entity/Entity;
    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/TridentEntity;getOwner()Lnet/minecraft/entity/Entity;"), ordinal = 0, method = "onEntityHit")
    public float spellboundTridentEntityOnEntityHitMixin(float h, EntityHitResult entityHitResult){
        Entity entity = ((TridentEntity)(Object)this).getOwner();
        return h + SBEnchantmentHelper.getThrownTridentDamage((TridentEntity)(Object)this, this.asItemStack(), entityHitResult.getEntity());
    }

    // Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(
    //  Lnet/minecraft/entity/LivingEntity;
    //  Lnet/minecraft/entity/LivingEntity;
    // )F
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;onTargetDamaged(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/Entity;)V"), method = "onEntityHit")
    public void spellboundTridentEntityOnEntityHitMixinTwo(EntityHitResult entityHitResult, CallbackInfo info){
        SBEnchantmentHelper.onThrownTridentEntityHit((TridentEntity)(Object)this,this.asItemStack(), entityHitResult.getEntity());
    }

    @Mixin(TridentEntity.class)
    public interface TridentEntityInvoker {
        @Invoker("asItemStack")
        public ItemStack invokeAsItemStack();
    }
}
