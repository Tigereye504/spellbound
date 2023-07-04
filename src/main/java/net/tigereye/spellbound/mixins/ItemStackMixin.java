package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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
            SBEnchantmentHelper.onItemDestroyed((ItemStack)(Object)this, player);
        }
    }

    @Inject(at = @At("HEAD"), method = "inventoryTick")
    public void spellboundItemStackInventoryTickMixin(World world, Entity entity, int slot, boolean selected, CallbackInfo ci){
        if(((ItemStack)(Object)this).hasEnchantments()){
            SBEnchantmentHelper.onInventoryTick((ItemStack)(Object)this, world, entity, slot, selected);
        }
    }
}
