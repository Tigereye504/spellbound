package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class DullnessEnchantment extends SBEnchantment {

    public DullnessEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.dullness.RARITY), EnchantmentCategory.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND}, false);
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
    public boolean isTreasureOnly() {return Spellbound.config.dullness.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.dullness.IS_FOR_SALE;}

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack) ||
                stack.getItem() instanceof TieredItem;

    }

    @Override
    public float getDamageBonus(int level, MobType group) {
        return -1.5f - level;
    }

}
