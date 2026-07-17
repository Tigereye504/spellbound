package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.tigereye.spellbound.interfaces.TridentEntityItemAccessor;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTrident.class)
public class TridentEntityMixin implements TridentEntityItemAccessor {

    @Shadow
    ItemStack tridentItem;

    public ItemStack spellbound_getTridentStack(){
        return tridentItem;
    }
    public void spellbound_setTridentStack(ItemStack tridentStack){
        this.tridentItem = tridentStack;
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/item/ItemStack;)V")
    public void spellboundTridentEntityInitMixin(Level world, LivingEntity entity, ItemStack stack, CallbackInfo info){
        SBEnchantmentHelper.onThrowTrident(entity,stack,(ThrownTrident)(Object)this);
    }

    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/projectile/ThrownTrident;getOwner()Lnet/minecraft/world/entity/Entity;"), ordinal = 0, method = "onHitEntity")
    public float spellboundTridentEntityOnEntityHitMixin(float h, EntityHitResult entityHitResult){
        return h + SBEnchantmentHelper.getThrownTridentDamage((ThrownTrident)(Object)this, spellbound_getTridentStack(), entityHitResult.getEntity());
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;doPostDamageEffects(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/Entity;)V"), method = "onHitEntity")
    public void spellboundTridentEntityOnEntityHitMixinTwo(EntityHitResult entityHitResult, CallbackInfo info){
        SBEnchantmentHelper.onThrownTridentEntityHit((ThrownTrident)(Object)this,spellbound_getTridentStack(), entityHitResult.getEntity());
    }

}
