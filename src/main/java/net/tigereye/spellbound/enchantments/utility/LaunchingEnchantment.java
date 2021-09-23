package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.SpellboundPlayerEntity;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;

public class LaunchingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public LaunchingEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 20;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level)+25;
    }

    @Override
    public int getMaxLevel() {
        if(Spellbound.config.LAUNCHING_ENABLED) return 2;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(user instanceof SpellboundPlayerEntity &&
                !(((SpellboundPlayerEntity)user).isMakingFullChargeAttack())){
            return;
        }
        target.setVelocity(target.getVelocity().x,Math.abs(target.getVelocity().y)+(level*.3),target.getVelocity().z);
        super.onTargetDamaged(user, target, level);
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
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
