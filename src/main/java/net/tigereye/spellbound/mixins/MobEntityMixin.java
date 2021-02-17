package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MobEntity.class)
public class MobEntityMixin {
    @ModifyVariable(at = @At(value = "CONSTANT", args = "floatValue=0F", ordinal = 0), ordinal = 0, method = "tryAttack")
    public float spellboundMobEntityTryAttackMixin(float h, Entity target){
        return h + SBEnchantmentHelper.getAttackDamage((LivingEntity)(Object)this, target);
    }
}
