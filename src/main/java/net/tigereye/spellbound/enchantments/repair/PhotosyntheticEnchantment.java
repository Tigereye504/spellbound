package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;

public class PhotosyntheticEnchantment extends SBEnchantment {

    public PhotosyntheticEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
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
        return Spellbound.config.PHOTOSYNTHETIC_ENABLED;
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
            int light = world.getLightLevel(entity.getBlockPos());
            if(light < Spellbound.config.PHOTOSYNTHETIC_LIGHT_MINIMUM){
                return;
            }
            int periodMultiplier = Math.max(1,16 - light);
            if(entity.world.getTime() % ((long) Spellbound.config.PHOTOSYNTHETIC_REPAIR_PERIOD*periodMultiplier) == 0){
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

}
