package net.tigereye.spellbound.mixins;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 1),
            ordinal = 0, method = "damage")
    public int spellboundItemStackUnbreakingMixin(int amount, int alsoAmount, Random random, ServerPlayerEntity player){
        Spellbound.LOGGER.info("amount = " + amount);
        Spellbound.LOGGER.info("alsoAmount = " + alsoAmount);
        return SBEnchantmentHelper.beforeDurabilityLoss((ItemStack)(Object)this,player,amount);
    }


    @Inject(at = @At(value = "RETURN"),method = "damage")
    public <T extends LivingEntity> void spellboundItemStackDamageMixin(int amount, Random random, ServerPlayerEntity player, CallbackInfoReturnable<Boolean> info){
        if(info.getReturnValue()){
            SBEnchantmentHelper.onToolBreak((ItemStack)(Object)this, player);
        }
    }
}
