package net.tigereye.spellbound.enchantments.utility.chestplate;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class WarlikeEnchantment extends SBEnchantment{

    public WarlikeEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.warlike.RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.warlike.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.warlike.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.warlike.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.warlike.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.warlike.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.warlike.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.warlike.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.warlike.IS_FOR_SALE;}

    @Override
    public float getLocalDifficultyModifier(int level, World world, PlayerEntity player, ItemStack itemStack) {
        return level*Spellbound.config.warlike.DIFFICULTY_PER_RANK;
    }
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack)
                || EnchantmentTarget.ARMOR.isAcceptableItem(stack.getItem());
    }
}
