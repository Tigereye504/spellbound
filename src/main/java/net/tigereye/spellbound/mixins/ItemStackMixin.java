package net.tigereye.spellbound.mixins;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import net.minecraft.util.math.random.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 1),
            ordinal = 0, method = "damage")
    public int spellboundItemStackUnbreakingMixin(int amount, int alsoAmount, Random random, ServerPlayerEntity player){
        return SBEnchantmentHelper.beforeDurabilityLoss((ItemStack)(Object)this,player,amount);
    }


    @Inject(at = @At(value = "RETURN"),method = "damage")
    public <T extends LivingEntity> void spellboundItemStackDamageMixin(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> info){
        if(info.getReturnValue()){
            SBEnchantmentHelper.onToolBreak((ItemStack)(Object)this, player);
        }
    }
}
