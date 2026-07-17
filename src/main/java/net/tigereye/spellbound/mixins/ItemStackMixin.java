package net.tigereye.spellbound.mixins;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @ModifyVariable(at = @At(value = "CONSTANT", args = "intValue=0", ordinal = 1),
            ordinal = 0, method = "hurt")
    public int spellboundItemStackUnbreakingMixin(int amount, int alsoAmount, RandomSource random, ServerPlayer player){
        return SBEnchantmentHelper.beforeDurabilityLoss((ItemStack)(Object)this,player,amount);
    }


    @Inject(at = @At(value = "RETURN"),method = "hurt")
    public <T extends LivingEntity> void spellboundItemStackDamageMixin(int amount, RandomSource random, ServerPlayer player, CallbackInfoReturnable<Boolean> info){
        if(info.getReturnValue()){
            SBEnchantmentHelper.onItemDestroyed((ItemStack)(Object)this, player);
        }
    }

    @Inject(at = @At("HEAD"), method = "inventoryTick")
    public void spellboundItemStackInventoryTickMixin(Level world, Entity entity, int slot, boolean selected, CallbackInfo ci){
        if(((ItemStack)(Object)this).isEnchanted()){
            SBEnchantmentHelper.onInventoryTick((ItemStack)(Object)this, world, entity, slot, selected);
        }
    }

    @Inject(at = @At("RETURN"), method = "useOn")
    public void spellboundItemStackOnUseMixin(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir){
        if(((ItemStack)(Object)this).isEnchanted()){
            SBEnchantmentHelper.onItemUse((ItemStack)(Object)this, context, cir.getReturnValue());
        }
    }
}
