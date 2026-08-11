package net.tigereye.spellbound.enchantments.utility.chestplate;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RepulsiveEnchantment extends SBEnchantment{


    public RepulsiveEnchantment() {
        super(definition(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE,
            Spellbound.config.repulsive.WEIGHT, //enchantment weight
            Spellbound.config.repulsive.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.repulsive.BASE_POWER,Spellbound.config.repulsive.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.repulsive.BASE_POWER+Spellbound.config.repulsive.POWER_RANGE,Spellbound.config.repulsive.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.repulsive.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.repulsive.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.repulsive.SOFT_CAP;}
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
