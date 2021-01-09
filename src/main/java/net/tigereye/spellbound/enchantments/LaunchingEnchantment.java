package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;

public class LaunchingEnchantment extends SBEnchantment {

    public LaunchingEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 20;
    }

    public int getMaxPower(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack)
                || EnchantmentTarget.WEAPON.isAcceptableItem(stack.getItem())
                || EnchantmentTarget.DIGGER.isAcceptableItem(stack.getItem())
                || stack.getItem() instanceof AxeItem;
                //TODO: implement for bows and crossbows
    }

    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(target instanceof LivingEntity) {
            ((LivingEntity) target).setVelocity(0,((LivingEntity) target).getVelocity().length(),0);
        }

        super.onTargetDamaged(user, target, level);
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }
}
