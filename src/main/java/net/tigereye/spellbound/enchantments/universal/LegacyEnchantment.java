package net.tigereye.spellbound.enchantments.universal;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;

import java.util.Iterator;
import java.util.Map;

public class LegacyEnchantment extends SBEnchantment {

    public LegacyEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    public int getMinPower(int level) {
        return 5;
    }

    public int getMaxPower(int level) {
        return 51;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.LEGACY_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 1;
        else return 0;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    public boolean isTreasure() {
        return false;
    }

    public void onToolBreak(int level, ItemStack itemStack, PlayerEntity entity) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        Map<Enchantment,Integer> enchants = EnchantmentHelper.get(itemStack);
        //enchants.remove(SBEnchantments.LEGACY);
        EnchantmentHelper.set(enchants,book);
        //TODO: onLegacyToolBreak(book,itemStack,entity) event
        if(!entity.giveItemStack(book)){
            entity.dropStack(book,0.5f);
        }
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.MENDING);
    }
}
