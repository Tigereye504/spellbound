package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class MetabolisingEnchantment extends SBEnchantment {

    public MetabolisingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.metabolising.RARITY), EnchantmentCategory.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.metabolising.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.metabolising.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.metabolising.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.metabolising.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.metabolising.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.metabolising.POWER_RANGE;}
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
