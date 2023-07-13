package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class DullnessEnchantment extends SBEnchantment {

    public DullnessEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.dullness.RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND}, false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.dullness.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.dullness.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.dullness.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.dullness.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.dullness.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.dullness.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.dullness.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.dullness.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) ||
                stack.getItem() instanceof ToolItem;

    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return -1.5f - level;
    }

}
