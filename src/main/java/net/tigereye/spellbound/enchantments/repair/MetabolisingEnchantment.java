package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class MetabolisingEnchantment extends SBEnchantment {

    public MetabolisingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.metabolising.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
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
    public boolean isTreasure() {return Spellbound.config.metabolising.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.metabolising.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.getWorld();
        if(!world.isClient() && stack.isDamaged()){
            if(!(entity instanceof PlayerEntity)){
                return;
            }
            HungerManager manager = ((PlayerEntity)entity).getHungerManager();
            if(manager.getFoodLevel() >= Spellbound.config.metabolising.FOOD_THRESHOLD){
                ((PlayerEntity) entity).addExhaustion(Spellbound.config.metabolising.EXHAUSTION_COST);
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }

}
