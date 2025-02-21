package net.tigereye.spellbound.mixins.client;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackClientMixin {
    @ModifyVariable(at = @At(value="CONSTANT", args = "intValue=10", ordinal = 0), ordinal = 0, method = "getTooltip")
    public List<Text> spellboundItemStackGetTooltipMixin(List<Text> list, PlayerEntity player, TooltipContext context){
        return SBEnchantmentHelper.addTooltip((ItemStack)(Object)this, list, player, context);
    }
}
