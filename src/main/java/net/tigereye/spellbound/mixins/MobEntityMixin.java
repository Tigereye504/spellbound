package net.tigereye.spellbound.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Mob.class)
public class MobEntityMixin {

    @ModifyVariable(at = @At(value = "INVOKE_ASSIGN",
            target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getDamageBonus(" +
                    "Lnet/minecraft/world/item/ItemStack;" +
                    "Lnet/minecraft/world/entity/EntityType;" +
                    ")F"),
            ordinal = 0,
            method = "doHurtTarget")
    public float spellboundMobEntityTryAttackMixin(float f, Entity target){
        return f + SBEnchantmentHelper.getDamageBonus((LivingEntity)(Object)this, target);
    }
}
