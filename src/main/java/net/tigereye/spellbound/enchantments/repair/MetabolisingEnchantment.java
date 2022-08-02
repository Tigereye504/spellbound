package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
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
        super(SpellboundUtil.rarityLookup(Spellbound.config.METABOLISING_RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return 5;
    }

    @Override
    public int getMaxPower(int level) {
        return 51;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.METABOLISING_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 1;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.world;
        if(!world.isClient() && stack.isDamaged()){
            if(!(entity instanceof PlayerEntity)){
                return;
            }
            HungerManager manager = ((PlayerEntity)entity).getHungerManager();
            if(manager.getFoodLevel() >= Spellbound.config.METABOLISING_FOOD_THRESHOLD){
                ((PlayerEntity) entity).addExhaustion(Spellbound.config.METABOLISING_EXHAUSTION_COST);
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

}