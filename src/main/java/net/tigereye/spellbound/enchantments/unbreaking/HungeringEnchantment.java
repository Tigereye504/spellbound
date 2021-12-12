package net.tigereye.spellbound.enchantments.unbreaking;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;

public class HungeringEnchantment extends SBEnchantment {

    public HungeringEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
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
        return Spellbound.config.HUNGERING_ENABLED;
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
        World world = entity.world;
        if(!world.isClient()){
            HungerManager manager = entity.getHungerManager();
            if(manager.getFoodLevel() >= Spellbound.config.HUNGERING_FOOD_THRESHOLD){
                entity.addExhaustion(loss*Spellbound.config.HUNGERING_EXHAUSTION_COST*2/(level+1));
                return 0;
            }
        }
        return loss;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.UNBREAKING);
    }
}
