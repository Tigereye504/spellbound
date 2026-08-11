package net.tigereye.spellbound.enchantments.utility.chestplate;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class WarlikeEnchantment extends SBEnchantment{

    public WarlikeEnchantment() {
        super(definition(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE,
            Spellbound.config.warlike.WEIGHT, //enchantment weight
            Spellbound.config.warlike.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.warlike.BASE_POWER,Spellbound.config.warlike.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.warlike.BASE_POWER+Spellbound.config.warlike.POWER_RANGE,Spellbound.config.warlike.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.warlike.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.warlike.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.warlike.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.warlike.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.warlike.IS_FOR_SALE;}

    @Override
    public float getLocalDifficultyModifier(int level, Level world, Player player, ItemStack itemStack) {
        return level*Spellbound.config.warlike.DIFFICULTY_PER_RANK;
    }
}
