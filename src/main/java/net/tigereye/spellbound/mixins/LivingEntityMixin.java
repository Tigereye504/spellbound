package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.spellbound.SpellboundLivingEntity;
import net.tigereye.spellbound.SpellboundPlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.mob_effect.SBStatusEffectHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin extends Entity implements SpellboundLivingEntity {

    private Vec3d SB_OldPos;
    private Vec3d SB_LastPos;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
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
    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getProtectionAmount(Ljava/lang/Iterable;Lnet/minecraft/entity/damage/DamageSource;)I"), ordinal = 0, method = "applyEnchantmentsToDamage")
    public int spellboundLivingEntityApplyEnchantmentsToDamageMixin(int k, DamageSource source, float amount){
        return SBEnchantmentHelper.getProtectionAmount(source,(LivingEntity)(Object)this,k,amount);
    }

    @Inject(at = @At("HEAD"), method = "baseTick")
    public void spellboundLivingEntityBaseTickMixin(CallbackInfo info){
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

    @Override
    public void updatePositionTracker(Vec3d pos) {
        SB_OldPos = SB_LastPos;
        SB_LastPos = pos;
    }

    @Override
    public Vec3d readPositionTracker() {
        return SB_OldPos;
    }
}
