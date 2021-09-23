package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;

public class MountedEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public MountedEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return (8 * level) - 3;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 20;
    }

    @Override
    public int getMaxLevel() {
        if(Spellbound.config.MOUNTED_ENABLED) return 5;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        if(attacker.hasVehicle()){
            return (Spellbound.config.MOUNTED_DAMAGE_PER_LEVEL * level) + Spellbound.config.MOUNTED_DAMAGE_BASE;
        }
        return 0;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker.getVehicle() != null){
            return damage * ((Spellbound.config.MOUNTED_PROJECTILE_PER_LEVEL * level) + Spellbound.config.MOUNTED_PROJECTILE_BASE);
        }
        return damage;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.SHARPNESS)
                && other.canCombine(Enchantments.POWER);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() == Items.BOOK;
    }
}
