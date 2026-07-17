package net.tigereye.spellbound.enchantments.utility.chestplate;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RepulsiveEnchantment extends SBEnchantment{


    public RepulsiveEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.repulsive.RARITY), EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.repulsive.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.repulsive.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.repulsive.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.repulsive.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.repulsive.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.repulsive.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.repulsive.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.repulsive.IS_FOR_SALE;}
    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(stack)) != stack){
            return;
        }
        SpellboundUtil.pushPullEntitiesPlayersInRange(Spellbound.config.repulsive.RANGE, -Spellbound.config.repulsive.STRENGTH, entity);
    }
}
