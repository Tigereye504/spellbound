package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.spellbound.interfaces.NextTickAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.mob_effect.SBStatusEffectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedList;
import java.util.List;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity implements SpellboundLivingEntity {

    private Vec3d SB_OldPos;
    private Vec3d SB_LastPos;
    private static final TrackedData<Float> SB_DurabilityBuffer = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private float SB_MaxDurabilityBuffer = 0;
    private final List<NextTickAction> nextTickActions = new LinkedList<>();

    public void addNextTickAction(NextTickAction action){
        nextTickActions.add(action);
    }

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker")
    public void HellishMaterialsInitDataTrackerMixin(CallbackInfo info){
        this.dataTracker.startTracking(SB_DurabilityBuffer,0f);
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
        for (NextTickAction action : nextTickActions) {
            action.act();
        }
        nextTickActions.clear();
        SBEnchantmentHelper.onTickAlways((LivingEntity)(Object)this);
        SBEnchantmentHelper.onTickWhileEquipped((LivingEntity)(Object)this);
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


    @Override
    public void setDurabilityBuffer(float buffer) {
        this.dataTracker.set(SB_DurabilityBuffer,buffer);
    }

    @Override
    public float getDurabilityBuffer() {
        return this.dataTracker.get(SB_DurabilityBuffer);
    }

    @Override
    public void setMaxDurabilityBuffer(float maxBuffer) {
        SB_MaxDurabilityBuffer = maxBuffer;
    }

    @Override
    public float getMaxDurabilityBuffer() {
        return SB_MaxDurabilityBuffer;
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
