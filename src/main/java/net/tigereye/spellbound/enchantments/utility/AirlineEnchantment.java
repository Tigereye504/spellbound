package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class AirlineEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public AirlineEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.AIRLINE_RARITY), EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.AIRLINE_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.AIRLINE_POWER_PER_RANK * level) - Spellbound.config.AIRLINE_BASE_POWER;
        if(level > Spellbound.config.AIRLINE_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.AIRLINE_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.AIRLINE_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() == Items.BOOK;
    }

    @Override
    public void onFireProjectile(int level, ItemStack itemStack, Entity entity, ProjectileEntity projectile){
        if(entity instanceof LivingEntity livingEntity){
            tetherTarget(level,projectile,livingEntity);
        }
    }

    @Override
    public void onThrowTrident(int level, ItemStack itemStack, Entity entity, TridentEntity projectile){
        if(entity instanceof LivingEntity livingEntity){
            tetherTarget(level,projectile,livingEntity);
        }
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        target.removeStatusEffect(SBStatusEffects.TETHERED);
        target.addStatusEffect(new TetheredInstance(anchor, Spellbound.config.AIRLINE_BASE_DURATION + (Spellbound.config.AIRLINE_DURATION_PER_RANK*level), 0));
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && other != Enchantments.RIPTIDE;
    }
    //doesn't support bows/crossbows because arrows usually dont survive impact
}
