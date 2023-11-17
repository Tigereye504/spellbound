package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.spellbound.interfaces.NextTickAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
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

    @Shadow protected float lastDamageTaken;
    @Unique
    private Vec3d SB_OldPos;
    @Unique
    private Vec3d SB_LastPos;
    @Unique
    private final List<NextTickAction> nextTickActions = new LinkedList<>();
    @Unique
    private final List<NextTickAction> nextTickActionsQueue = new LinkedList<>();
    @Unique
    private boolean performingNextTickActions = false;
    @Unique
    private int graceTicks = 0;
    @Unique
    private float graceMagnitude = 0;

    public void spellbound$addNextTickAction(NextTickAction action){
        if (performingNextTickActions)
            nextTickActionsQueue.add(action);
        else
            nextTickActions.add(action);
    }

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    //@Inject(at = @At("TAIL"), method = "initDataTracker")
    //public void HellishMaterialsInitDataTrackerMixin(CallbackInfo info){
    //    this.dataTracker.startTracking(SB_DurabilityBuffer,0f);
    //}


    @Inject(at = @At(value = "RETURN"),method = "getArmor", cancellable = true)
    public void spellboundLivingEntityGetArmorMixin(CallbackInfoReturnable<Integer> info){
        info.setReturnValue(info.getReturnValueI() + SBEnchantmentHelper.getArmorAmount((LivingEntity)(Object)this));
    }

    @Inject(method = "damage", at = @At(value = "INVOKE", target="Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/entity/damage/DamageSource;F)V",ordinal = 1))
    public void spellboundLivingEntityApplyIFramesDurationMixin(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir){
        this.lastDamageTaken = SBEnchantmentHelper.onApplyIFrameMagnitude(this.lastDamageTaken, source, amount, (LivingEntity)(Object)this);
        int duration = SBEnchantmentHelper.onApplyIFrameDuration(this.timeUntilRegen, source, amount, (LivingEntity)(Object)this);
        if(!this.getWorld().isClient && ((LivingEntity)(Object)this) instanceof ServerPlayerEntity entity){
            NetworkingUtil.sendGraceDataPacket(this.lastDamageTaken,duration-10,entity);
        }
        this.timeUntilRegen = duration;
    }

    @ModifyVariable(at = @At("HEAD"), ordinal = 0, method = "applyArmorToDamage")
    public float spellboundLivingEntityApplyArmorMixin(float amount, DamageSource source){
        amount = SBEnchantmentHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
        return SBStatusEffectHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
    }

    @Inject(at = @At(value="CONSTANT", args="floatValue=0",ordinal = 1), method = "applyDamage")
    public void spellboundLivingEntityApplyDamagePostDamageMixin(DamageSource source, float amount, CallbackInfo info){
        SBEnchantmentHelper.onRedHealthDamage(source,(LivingEntity)(Object)this,amount);
        if(source.getAttacker() instanceof LivingEntity attacker) {
            SBEnchantmentHelper.onDoRedHealthDamage(attacker, source, (LivingEntity) (Object) this, amount);
        }
    }

    //Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(
    //  Ljava/lang/Iterable;
    //  Lnet/minecraft/entity/damage/DamageSource;
    //)I
    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I"), ordinal = 0, method = "modifyAppliedDamage")
    public int spellboundLivingEntityApplyEnchantmentsToDamageMixin(int k, DamageSource source, float amount){
        return SBEnchantmentHelper.getProtectionAmount(source,(LivingEntity)(Object)this,k,amount);
    }

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void spellboundLivingEntityBaseTickMixin(CallbackInfo info){
        performingNextTickActions = true;
        for (NextTickAction action : nextTickActions) {
            action.act();
        }
        nextTickActions.clear();
        nextTickActions.addAll(nextTickActionsQueue);
        nextTickActionsQueue.clear();
        performingNextTickActions = false;
        SBEnchantmentHelper.onTickAlways((LivingEntity)(Object)this);
        SBEnchantmentHelper.onTickWhileEquipped((LivingEntity)(Object)this);
        if(graceTicks > 0){
            --graceTicks;
        }
    }

    @Inject(at = @At("HEAD"), method = "tryUseTotem", cancellable = true)
    public void spellboundLivingEntityTryUseTotemMixin(DamageSource source, CallbackInfoReturnable<Boolean> cir){
        if(SBEnchantmentHelper.onLethalDamage(source,(LivingEntity)(Object)this)){
            cir.setReturnValue(true);
        }
    }

    //@Inject(at = @At("HEAD"), method = "onKilledBy")
    //Lnet/minecraft/entity/LivingEntity;onKilledBy(
    //  Lnet/minecraft/entity/LivingEntity;
    //)V
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onKilledBy(Lnet/minecraft/entity/LivingEntity;)V"), method = "onDeath")
    public void spellboundLivingEntityOnDeathMixin(DamageSource source, CallbackInfo info){
        SBEnchantmentHelper.onDeath(source,(LivingEntity) (Object) this);
    }

    @Inject(method = "getEquipmentChanges", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void spellboundLivingEntityOnEquipmentChange(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, Map<EquipmentSlot, ItemStack> changes, EquipmentSlot[] slots, int slotsSize, int slotIndex, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack) {
        SBEnchantmentHelper.onEquipmentChange((LivingEntity) (Object) this, equipmentSlot, previousStack, currentStack);
    }

    @Inject(at = @At("TAIL"), method = "jump")
    public void spellboundLivingEntityJumpMixin(CallbackInfo info){
        SBEnchantmentHelper.onJump((LivingEntity)(Object)this);
    }

    @Override
    public void spellbound$updatePositionTracker(Vec3d pos) {
        SB_OldPos = SB_LastPos;
        SB_LastPos = pos;
    }

    @Override
    public Vec3d spellbound$readPositionTracker() {
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
}
