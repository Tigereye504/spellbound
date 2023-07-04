package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.Map;

public class LegacyEnchantment extends SBEnchantment {

    public LegacyEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.legacy.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.legacy.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.legacy.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.legacy.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.legacy.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.legacy.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.legacy.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.legacy.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.legacy.IS_FOR_SALE;}

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onToolBreak(int level, ItemStack itemStack, Entity entity) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        Map<Enchantment,Integer> enchants = EnchantmentHelper.get(itemStack);
        EnchantmentHelper.set(enchants,book);
        SBEnchantmentHelper.onLegacyToolBreak(book,itemStack,entity);
        if(entity instanceof PlayerEntity pEntity) {
            if (!pEntity.giveItemStack(book)) {
                entity.dropStack(book, 0.5f);
            }
        }
        else{
            entity.dropStack(book, 0.5f);
        }
    }

}
