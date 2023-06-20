package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
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
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity implements SpellboundLivingEntity {

    @Shadow protected float lastDamageTaken;
    private Vec3d SB_OldPos;
    private Vec3d SB_LastPos;
    private final List<NextTickAction> nextTickActions = new LinkedList<>();
    private final List<NextTickAction> nextTickActionsQueue = new LinkedList<>();
    private boolean performingNextTickActions = false;
    private int graceTicks = 0;
    private float graceMagnitude = 0;

    public void addNextTickAction(NextTickAction action){
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

    @ModifyConstant(method = "damage", constant = @Constant(intValue = 20))
    public int spellboundLivingEntityApplyIFramesDurationMixin(int frames, DamageSource source, float amount){

        this.lastDamageTaken = SBEnchantmentHelper.onApplyIFrameMagnitude(this.lastDamageTaken, source, amount, (LivingEntity)(Object)this);
        int duration = SBEnchantmentHelper.onApplyIFrameDuration(frames, source, amount, (LivingEntity)(Object)this);
        if(!this.world.isClient && ((LivingEntity)(Object)this) instanceof ServerPlayerEntity entity){
            NetworkingUtil.sendGraceDataPacket(this.lastDamageTaken,duration-10,entity);
        }
        return duration;

    }

    @ModifyVariable(at = @At("HEAD"), ordinal = 0, method = "applyArmorToDamage")
    public float spellboundLivingEntityApplyArmorMixin(float amount, DamageSource source){
        amount = SBEnchantmentHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
        return SBStatusEffectHelper.onPreArmorDefense(source,(LivingEntity)(Object)this,amount);
    }

    @Inject(at = @At(value="CONSTANT", args="floatValue=0",ordinal = 1), method = "applyDamage")
    public void spellboundLivingEntityApplyDamagePostDamageMixin(DamageSource source, float amount, CallbackInfo info){
        SBEnchantmentHelper.onRedHealthDamage(source,(LivingEntity)(Object)this,amount);
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

    //@Inject(at = @At("HEAD"), method = "onKilledBy")
    //Lnet/minecraft/entity/LivingEntity;onKilledBy(
    //  Lnet/minecraft/entity/LivingEntity;
    //)V
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;onKilledBy(Lnet/minecraft/entity/LivingEntity;)V"), method = "onDeath")
    public void spellboundLivingEntityOnDeathMixin(DamageSource source, CallbackInfo info){
        SBEnchantmentHelper.onDeath(source,(LivingEntity) (Object) this);
    }

    @Inject(at = @At("TAIL"), method = "jump")
    public void spellboundLivingEntityJumpMixin(CallbackInfo info){
        SBEnchantmentHelper.onJump((LivingEntity)(Object)this);
    }

    @Override
    public void updatePositionTracker(Vec3d pos) {
        SB_OldPos = SB_LastPos;
        SB_LastPos = pos;
    }

    @Override
    public Vec3d readPositionTracker() {
        return SB_OldPos;
    }

    public float getGraceMagnitude(){
        return graceMagnitude;
    }
    public int getGraceTicks(){
        return graceTicks;
    }
    public void setGraceMagnitude(float lastDamageTaken){
        this.graceMagnitude = lastDamageTaken;
    }

    public void setGraceTicks(int iFrameTicks){
        graceTicks = iFrameTicks;
    }

    @Shadow
    protected void initDataTracker() {

    }

    @Shadow
    protected void readCustomDataFromNbt(NbtCompound tag) {

    }

    @Shadow
    protected void writeCustomDataToNbt(NbtCompound tag) {

    }

    @Shadow
    public Packet<?> createSpawnPacket() {
        return null;
    }
}
