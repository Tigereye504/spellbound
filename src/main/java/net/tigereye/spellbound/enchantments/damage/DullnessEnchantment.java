package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.*;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class DullnessEnchantment extends SBEnchantment {

    public DullnessEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.DULLNESS_RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.DULLNESS_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        return 1 + 8 * (level - 1);
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) || EnchantmentTarget.BREAKABLE.isAcceptableItem(stack.getItem());
    }

    @Override
    public float getAttackDamage(int level, EntityGroup group) {
        return -1.5f - level;
    }

}
