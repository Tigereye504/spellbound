package net.tigereye.spellbound.mixins;

import com.google.common.collect.Maps;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.mob_effect.SBStatusEffectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements SpellboundLivingEntity {

    @Shadow protected float lastHurt;
    @Shadow private final Map<MobEffect, MobEffectInstance> activeEffects = Maps.newHashMap();
    @Unique
    private Vec3 SB_OldPos;
    @Unique
    private Vec3 SB_LastPos;
    @Unique
    private final List<DelayedAction> delayedActions = new LinkedList<>();
    @Unique
    private final List<DelayedAction> delayedActionsQueue = new LinkedList<>();
    @Unique
    private boolean performingDelayedActions = false;
    @Unique
    private int graceTicks = 0;
    @Unique
    private float graceMagnitude = 0;
    @Unique
    private static final EntityDataAccessor<Boolean> SHIELDED = SynchedEntityData.defineId(LivingEntity.class, EntityDataSerializers.BOOLEAN);

    public void spellbound$addDelayedAction(DelayedAction action){
        if (performingDelayedActions)
            delayedActionsQueue.add(action);
        else
            delayedActions.add(action);
    }

    public List<DelayedAction> spellbound$getDelayedActions(){
        return delayedActions;
    }

    public LivingEntityMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    //@Inject(at = @At("TAIL"), method = "initDataTracker")
    //public void HellishMaterialsInitDataTrackerMixin(CallbackInfo info){
    //    this.dataTracker.startTracking(SB_DurabilityBuffer,0f);
    //}


    @Inject(at = @At(value = "RETURN"),method = "getArmorValue", cancellable = true)
    public void spellboundLivingEntityGetArmorMixin(CallbackInfoReturnable<Integer> info){
        info.setReturnValue(info.getReturnValueI() + SBEnchantmentHelper.getArmorAmount((LivingEntity)(Object)this));
    }

    @Inject(method = "hurt", at = @At(value = "INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V",ordinal = 1))
    public void spellboundLivingEntityApplyIFramesDurationMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        this.lastHurt = SBEnchantmentHelper.onApplyIFrameMagnitude(this.lastHurt, source, amount, (LivingEntity)(Object)this);
        int duration = SBEnchantmentHelper.onApplyIFrameDuration(this.invulnerableTime, source, amount, (LivingEntity)(Object)this);
        if(!this.level().isClientSide && ((LivingEntity)(Object)this) instanceof ServerPlayer entity){
            NetworkingUtil.sendGraceDataPacket(this.lastHurt,duration-10,entity);
        }
        this.invulnerableTime = duration;
    }

    @ModifyVariable(at = @At("HEAD"), ordinal = 0, method = "getDamageAfterArmorAbsorb")
    public float spellboundLivingEntityApplyArmorMixin(float amount, DamageSource source){
        amount = SBEnchantmentHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
        return SBStatusEffectHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
    }

    @Inject(at = @At(value="CONSTANT", args="floatValue=0",ordinal = 1), method = "actuallyHurt")
    public void spellboundLivingEntityApplyDamagePostDamageMixin(DamageSource source, float amount, CallbackInfo info){
        SBEnchantmentHelper.onRedHealthDamage(source,(LivingEntity)(Object)this,amount);
        if(source.getEntity() instanceof LivingEntity attacker) {
            SBEnchantmentHelper.onDoRedHealthDamage(attacker, source, (LivingEntity) (Object) this, amount);
        }
    }

    //Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(
    //  Ljava/lang/Iterable;
    //  Lnet/minecraft/entity/damage/DamageSource;
    //)I
    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageProtection(Ljava/lang/Iterable;Lnet/minecraft/world/damagesource/DamageSource;)I"), ordinal = 0, method = "getDamageAfterMagicAbsorb")
    public int spellboundLivingEntityApplyEnchantmentsToDamageMixin(int k, DamageSource source, float amount){
        return SBEnchantmentHelper.getProtectionAmount(source,(LivingEntity)(Object)this,k,amount);
    }

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void spellboundLivingEntityBaseTickMixin(CallbackInfo info){
        performingDelayedActions = true;
        for (DelayedAction action : delayedActions) {
            if(action.actOrDecrementTicks()){
                delayedActionsQueue.add(action);
            }
        }
        delayedActions.clear();
        delayedActions.addAll(delayedActionsQueue);
        delayedActionsQueue.clear();
        performingDelayedActions = false;
        SBEnchantmentHelper.onTickAlways((LivingEntity)(Object)this);
        SBEnchantmentHelper.onTickWhileEquipped((LivingEntity)(Object)this);
        if(graceTicks > 0){
            --graceTicks;
        }
    }

    @Inject(at = @At("HEAD"), method = "checkTotemDeathProtection", cancellable = true)
    public void spellboundLivingEntityTryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if(SBEnchantmentHelper.onLethalDamage(source,(LivingEntity)(Object)this)){
            cir.setReturnValue(true);
        }
    }

    //@Inject(at = @At("HEAD"), method = "onKilledBy")
    //Lnet/minecraft/entity/LivingEntity;onKilledBy(
    //  Lnet/minecraft/entity/LivingEntity;
    //)V
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;createWitherRose(Lnet/minecraft/world/entity/LivingEntity;)V"), method = "die")
    public void spellboundLivingEntityOnDeathMixin(DamageSource source, CallbackInfo info){
        SBEnchantmentHelper.onDeath(source,(LivingEntity) (Object) this);
        SBStatusEffectHelper.onDeath(source,(LivingEntity) (Object) this);
    }

    @Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void spellboundLivingEntityOnEquipmentChange(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> changes, EquipmentSlot[] slots, int slotsSize, int slotIndex, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {
        SBEnchantmentHelper.onEquipmentChange((LivingEntity) (Object) this, equipmentSlot, previousStack, currentStack);
    }

    @Inject(at = @At("TAIL"), method = "jumpFromGround")
    public void spellboundLivingEntityJumpMixin(CallbackInfo info){
        SBEnchantmentHelper.onJump((LivingEntity)(Object)this);
    }

    @Inject(at = @At("HEAD"), method = "defineSynchedData")
    public void spellboundLivingEntityInitDataTracker(CallbackInfo info){
        this.entityData.define(SHIELDED, false);
    }

    @Inject(at = @At("HEAD"), method = "updateEffectVisibility")
    public void spellboundLivingEntityUpdatePotionVisibilityMixin(CallbackInfo info){
        this.entityData.set(SHIELDED, this.activeEffects.containsKey(SBStatusEffects.SHIELDED));
    }

    @Override
    public void spellbound$updatePositionTracker(Vec3 pos) {
        SB_OldPos = SB_LastPos;
        SB_LastPos = pos;
    }

    @Override
    public Vec3 spellbound$readPositionTracker() {
        return SB_OldPos;
    }

    public float spellbound$getGraceMagnitude(){
        return graceMagnitude;
    }
    public int spellbound$getGraceTicks(){
        return graceTicks;
    }
    public void spellbound$setGraceMagnitude(float lastDamageTaken){
        this.graceMagnitude = lastDamageTaken;
    }

    public void spellbound$setGraceTicks(int iFrameTicks){
        graceTicks = iFrameTicks;
    }
    public boolean spellbound$shouldDisplayShielded(){return this.entityData.get(SHIELDED);}
}
