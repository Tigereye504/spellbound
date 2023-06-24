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
import net.tigereye.spellbound.util.SpellboundUtil;

public class MountedEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public MountedEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.MOUNTED_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.MOUNTED_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.MOUNTED_POWER_PER_RANK * level) + Spellbound.config.MOUNTED_BASE_POWER;
        if(level > Spellbound.config.MOUNTED_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.MOUNTED_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.MOUNTED_HARD_CAP;
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
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() == Items.BOOK;
    }
}
