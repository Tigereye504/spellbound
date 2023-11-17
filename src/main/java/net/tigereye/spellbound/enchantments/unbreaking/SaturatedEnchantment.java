package net.tigereye.spellbound.enchantments.unbreaking;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SaturatedEnchantment extends SBEnchantment {

    public SaturatedEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.saturated.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.saturated.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.saturated.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.saturated.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.saturated.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.saturated.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.saturated.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.saturated.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.saturated.IS_FOR_SALE;}
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayerEntity entity, int loss){
        if(entity == null){
            return loss;
        }
        World world = entity.getWorld();
        if(!world.isClient()){
            HungerManager manager = entity.getHungerManager();
            if(manager.getFoodLevel() >= Spellbound.config.saturated.FOOD_THRESHOLD){
                float cost = loss*Spellbound.config.saturated.EXHAUSTION_COST *2/(level+1);
                entity.addExhaustion(loss*Spellbound.config.saturated.EXHAUSTION_COST *2/(level+1));
                if(Spellbound.DEBUG){
                    Spellbound.LOGGER.info("Hungering prevented "+loss+" durability loss for " +cost+" exhaustion");
                    Spellbound.LOGGER.info(manager.getSaturationLevel() + " saturation remains with " + manager.getExhaustion() + " exhaustion");
                }
                return 0;
            }
        }
        return loss;
    }

}
