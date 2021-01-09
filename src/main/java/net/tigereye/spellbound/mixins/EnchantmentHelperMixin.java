package net.tigereye.spellbound.mixins;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {

    //check for CustomConditionsEnchantments
    @Inject(at = @At("RETURN"), method = "getPossibleEntries", cancellable = true)
    private static void spellboundEnchantmentHelperGetPossibleEntriesMixin(int power, ItemStack stack, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> info) {
        List<EnchantmentLevelEntry> oldlist = info.getReturnValue();
        List<EnchantmentLevelEntry> newlist = new ArrayList<>();
        Iterator<EnchantmentLevelEntry> var6 = oldlist.iterator();

        while(var6.hasNext()) {
            EnchantmentLevelEntry ele = var6.next();
            if(!(ele.enchantment instanceof CustomConditionsEnchantment)){
                newlist.add(ele);
            }
            else if(((CustomConditionsEnchantment)ele.enchantment).isAcceptableAtTable(stack)){
                newlist.add(ele);
            }
        }
        info.setReturnValue(newlist);
    }
}
