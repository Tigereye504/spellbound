package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;


public class LegacyEnchantment extends SBEnchantment {

    public LegacyEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            Spellbound.config.legacy.WEIGHT, //enchantment weight
            Spellbound.config.legacy.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.legacy.BASE_POWER,Spellbound.config.legacy.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.legacy.BASE_POWER+Spellbound.config.legacy.POWER_RANGE,Spellbound.config.legacy.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.legacy.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.MAINHAND}),
            false); //can work outside of prefered slot
    }
    public boolean isEnabled() {return Spellbound.config.legacy.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.legacy.SOFT_CAP;}
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
        EnchantmentHelper.setEnchantments(book,EnchantmentHelper.getEnchantmentsForCrafting(itemStack));
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
