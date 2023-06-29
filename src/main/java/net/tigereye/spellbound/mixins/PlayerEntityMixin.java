package net.tigereye.spellbound.mixins;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements SpellboundPlayerEntity {

    boolean spellboundEnchantments_IsMakingFullChargeAttack = false;

    @ModifyVariable(at = @At(value = "CONSTANT", args = "floatValue=0.5F", ordinal = 0), ordinal = 1, method = "attack")
    public float spellboundPlayerEntityAttackMixin(float h, Entity target){
        return h + SBEnchantmentHelper.getAttackDamage((PlayerEntity)(Object)this, target);
    }

    @Inject(at = @At(value="CONSTANT", args="floatValue=0",ordinal = 1), method = "applyDamage")
    public void spellboundLivingEntityApplyDamagePostDamageMixin(DamageSource source, float amount, CallbackInfo info){
        SBEnchantmentHelper.onRedHealthDamage(source,(LivingEntity)(Object)this,amount);
        if(source.getAttacker() instanceof LivingEntity attacker) {
            SBEnchantmentHelper.onDoRedHealthDamage(attacker, source, (LivingEntity) (Object) this, amount);
        }
    }

    @Override
    public void setIsMakingFullChargeAttack(boolean set) {
        spellboundEnchantments_IsMakingFullChargeAttack = set;
    }

    @Override
    public boolean isMakingFullChargeAttack() {
        return spellboundEnchantments_IsMakingFullChargeAttack;
    }

    //Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V"), method = "attack")
    public void spellboundPlayerEntityAttackMixin(CallbackInfo info){
        setIsMakingFullChargeAttack(((PlayerEntity)(Object)this).getAttackCooldownProgress(0.5F) == 1);
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
