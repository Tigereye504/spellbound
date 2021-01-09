package net.tigereye.spellbound.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.tigereye.spellbound.enchantments.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin{
    @ModifyVariable(at = @At(value = "CONSTANT", args = "floatValue=0.5F", ordinal = 0), ordinal = 1, method = "attack")
    public float spellboundPlayerEntityAttackMixin(float h, Entity target){
        return h + SBEnchantmentHelper.getAttackDamage((PlayerEntity)(Object)this, target);
    }

    //Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(
    //   Lnet/minecraft/entity/LivingEntity;
    //)B
    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(Lnet/minecraft/entity/LivingEntity;)Z"), ordinal = 0 ,method = "getBlockBreakingSpeed")
    public float spellboundPlayerEntityGetBlockBreakingSpeedMixin(float f, BlockState block){
        return SBEnchantmentHelper.getMiningSpeed((PlayerEntity)(Object)this, block, f);
    }

    //interact(Entity entity, Hand hand)
    @Inject(at = @At(value = "HEAD"),method = "interact")
    public void spellboundPlayerEntityInteractMixin(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> info){
        SBEnchantmentHelper.onActivate((PlayerEntity)(Object)this, entity, hand);
    }
}
