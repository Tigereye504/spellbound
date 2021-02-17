package net.tigereye.spellbound.mixins;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(ItemStack.class)
public class ItemStackClientMixin {
    @ModifyVariable(at = @At(value="CONSTANT", args = "intValue=10", ordinal = 0), ordinal = 0, method = "getTooltip")
    public List<Text> spellboundItemStackGetTooltipMixin(List<Text> list, PlayerEntity player, TooltipContext context){
        return SBEnchantmentHelper.addTooltip((ItemStack)(Object)this, list, player, context);
    }
}
