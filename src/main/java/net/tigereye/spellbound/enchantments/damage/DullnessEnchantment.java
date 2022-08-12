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
        int power = (Spellbound.config.DULLNESS_POWER_PER_RANK * level) - Spellbound.config.DULLNESS_BASE_POWER;
        if(level > Spellbound.config.DULLNESS_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.DULLNESS_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.DULLNESS_HARD_CAP;
        else return 0;
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
