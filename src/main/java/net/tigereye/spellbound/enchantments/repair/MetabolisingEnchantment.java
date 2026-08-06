package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class MetabolisingEnchantment extends SBEnchantment {

    public MetabolisingEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.metabolising.RARITY), //enchantment weight
            Spellbound.config.metabolising.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.metabolising.BASE_POWER,Spellbound.config.metabolising.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.metabolising.BASE_POWER+Spellbound.config.metabolising.POWER_RANGE,Spellbound.config.metabolising.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.metabolising.RARITY-1), //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.MAINHAND}),
            false); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.metabolising.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.metabolising.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.metabolising.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.metabolising.IS_FOR_SALE;}

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        Level world = entity.level();
        if(!world.isClientSide() && stack.isDamaged()){
            if(!(entity instanceof Player)){
                return;
            }
            FoodData manager = ((Player)entity).getFoodData();
            if(manager.getFoodLevel() >= Spellbound.config.metabolising.FOOD_THRESHOLD){
                ((Player) entity).causeFoodExhaustion(Spellbound.config.metabolising.EXHAUSTION_COST);
                stack.setDamageValue(stack.getDamageValue()-1);
            }
        }
    }

}
