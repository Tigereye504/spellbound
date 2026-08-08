package net.tigereye.spellbound.mixins;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerEntityMixin implements SpellboundPlayerEntity {

    @Unique
    boolean spellboundEnchantments_IsMakingFullChargeAttack = false;

    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(" +
                    "Lnet/minecraft/world/item/ItemStack;" +
                    "Lnet/minecraft/world/entity/EntityType;" +
                    ")F"),
            //require = 2,
            ordinal = 1,
            method= "attack")
    public float spellboundPlayerEntityAttackMixin(float g, Entity target){
        return g + SBEnchantmentHelper.getDamageBonus((Player)(Object)this, target);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;setHealth(F)V", shift = At.Shift.AFTER), method = "actuallyHurt")
    public void spellboundLivingEntityApplyDamagePostDamageMixin(DamageSource source, float amount, CallbackInfo info){
        SBEnchantmentHelper.onTakeRedHealthDamage(source,(LivingEntity)(Object)this,amount);
        if(source.getEntity() instanceof LivingEntity attacker) {
            SBEnchantmentHelper.onDoRedHealthDamage(attacker, source, (LivingEntity) (Object) this, amount);
        }
    }

    @Override
    public void spellbound$setIsMakingFullChargeAttack(boolean set) {
        spellboundEnchantments_IsMakingFullChargeAttack = set;
    }

    @Override
    public boolean spellbound$isMakingFullChargeAttack() {
        return spellboundEnchantments_IsMakingFullChargeAttack;
    }

    //Lnet/minecraft/entity/player/PlayerEntity;resetLastAttackedTicks()V
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;resetAttackStrengthTicker()V"), method = "attack")
    public void spellboundPlayerEntityAttackMixin(CallbackInfo info){
        spellbound$setIsMakingFullChargeAttack(((Player)(Object)this).getAttackStrengthScale(0.5F) == 1);
    }

    //Lnet/minecraft/entity/effect/StatusEffectUtil;hasHaste(
    //   Lnet/minecraft/entity/LivingEntity;
    //)B
    @ModifyVariable(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/effect/MobEffectUtil;hasDigSpeed(Lnet/minecraft/world/entity/LivingEntity;)Z"), ordinal = 0 ,method = "getDestroySpeed")
    public float spellboundPlayerEntityGetBlockBreakingSpeedMixin(float f, BlockState block){
        return SBEnchantmentHelper.getMiningSpeed((Player)(Object)this, block, f);
    }

    //interact(Entity entity, Hand hand)
    @Inject(at = @At(value = "HEAD"),method = "interactOn")
    public void spellboundPlayerEntityInteractMixin(Entity entity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> info){
        SBEnchantmentHelper.onActivate((Player)(Object)this, entity, hand);
    }

}
