package net.tigereye.spellbound.enchantments.unbreaking;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
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
        super(SpellboundUtil.rarityLookup(Spellbound.config.SATURATED_RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.SATURATED_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 3;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayerEntity entity, int loss){
        if(entity == null){
            return loss;
        }
        World world = entity.world;
        if(!world.isClient()){
            HungerManager manager = entity.getHungerManager();
            if(manager.getFoodLevel() >= Spellbound.config.SATURATED_FOOD_THRESHOLD){
                float cost = loss*Spellbound.config.SATURATED_EXHAUSTION_COST *2/(level+1);
                entity.addExhaustion(loss*Spellbound.config.SATURATED_EXHAUSTION_COST *2/(level+1));
                if(Spellbound.DEBUG){
                    Spellbound.LOGGER.info("Hungering prevented "+loss+" durability loss for " +cost+" exhaustion");
                    Spellbound.LOGGER.info(manager.getSaturationLevel() + " saturation remains with " + manager.getExhaustion() + " exhaustion");
                }
                return 0;
            }
        }
        return loss;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

}
