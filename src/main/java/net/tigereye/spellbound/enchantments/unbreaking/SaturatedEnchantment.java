package net.tigereye.spellbound.enchantments.unbreaking;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SaturatedEnchantment extends SBEnchantment {

    public SaturatedEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            Spellbound.config.saturated.WEIGHT, //enchantment weight
            Spellbound.config.saturated.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.saturated.BASE_POWER,Spellbound.config.saturated.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.saturated.BASE_POWER+Spellbound.config.saturated.POWER_RANGE,Spellbound.config.saturated.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.saturated.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            false); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.saturated.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.saturated.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.saturated.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.saturated.IS_FOR_SALE;}
    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayer entity, int loss){
        if(entity == null){
            return loss;
        }
        Level world = entity.level();
        if(!world.isClientSide()){
            FoodData manager = entity.getFoodData();
            if(manager.getFoodLevel() >= Spellbound.config.saturated.FOOD_THRESHOLD){
                float cost = loss*Spellbound.config.saturated.EXHAUSTION_COST *2/(level+1);
                entity.causeFoodExhaustion(loss*Spellbound.config.saturated.EXHAUSTION_COST *2/(level+1));
                if(Spellbound.DEBUG){
                    Spellbound.LOGGER.info("Hungering prevented "+loss+" durability loss for " +cost+" exhaustion");
                    Spellbound.LOGGER.info(manager.getSaturationLevel() + " saturation remains with " + manager.getExhaustionLevel() + " exhaustion");
                }
                return 0;
            }
        }
        return loss;
    }

}
