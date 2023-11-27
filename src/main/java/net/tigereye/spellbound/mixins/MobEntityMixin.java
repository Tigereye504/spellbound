package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/enchantment/EnchantmentHelper;getAttackDamage(" +
                    "Lnet/minecraft/item/ItemStack;" +
                    "Lnet/minecraft/entity/EntityGroup;" +
                    ")F"),
            ordinal = 0,
            method = "tryAttack")
    public float spellboundMobEntityTryAttackMixin(float f, Entity target){
        return f + SBEnchantmentHelper.getAttackDamage((LivingEntity)(Object)this, target);
    }
}
