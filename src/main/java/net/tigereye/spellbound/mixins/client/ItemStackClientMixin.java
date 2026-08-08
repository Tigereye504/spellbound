package net.tigereye.spellbound.mixins.client;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemStackClientMixin {
    //@ModifyVariable(at = @At(value="CONSTANT", args = "intValue=10", ordinal = 0), ordinal = 0, method = "getTooltipLines")
    @ModifyVariable(at = @At(value="RETURN"), ordinal = 0, method = "getTooltipLines")
    public List<Component> spellboundItemStackGetTooltipMixin(List<Component> list, Item.TooltipContext tooltipContext, Player player, TooltipFlag context) {
        return SBEnchantmentHelper.addTooltip((ItemStack)(Object)this, list, player, context);
    }
}
