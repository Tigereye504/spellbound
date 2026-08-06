package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class DullnessEnchantment extends SBEnchantment {

    public DullnessEnchantment() {
        super(definition(ItemTags.WEAPON_ENCHANTABLE, ItemTags.MINING_ENCHANTABLE, //enchantment targets: supports weapons, primarily for tools
            SpellboundUtil.rarityLookup(Spellbound.config.dullness.RARITY), //enchantment weight
            Spellbound.config.dullness.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.dullness.BASE_POWER,Spellbound.config.dullness.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.dullness.BASE_POWER+Spellbound.config.dullness.POWER_RANGE,Spellbound.config.dullness.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.dullness.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.dullness.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.dullness.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.dullness.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.dullness.IS_FOR_SALE;}

    @Override
    public float getDamageBonus(int level, EntityType<?> entityType) {
        return -1.5f - level;
    }

}
