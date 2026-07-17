package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.Map;

public class LegacyEnchantment extends SBEnchantment {

    public LegacyEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.legacy.RARITY), EnchantmentCategory.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
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
    public boolean isTreasureOnly() {return Spellbound.config.legacy.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.legacy.IS_FOR_SALE;}

    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public void onToolBreak(int level, ItemStack itemStack, Entity entity) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);
        Map<Enchantment,Integer> enchants = EnchantmentHelper.getEnchantments(itemStack);
        EnchantmentHelper.setEnchantments(enchants,book);
        SBEnchantmentHelper.onLegacyToolBreak(book,itemStack,entity);
        if(entity instanceof Player pEntity) {
            if (!pEntity.addItem(book)) {
                entity.spawnAtLocation(book, 0.5f);
            }
        }
        else{
            entity.spawnAtLocation(book, 0.5f);
        }
    }

}
