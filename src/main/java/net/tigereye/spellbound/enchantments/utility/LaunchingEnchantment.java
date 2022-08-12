package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class LaunchingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public LaunchingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.LAUNCHING_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.LAUNCHING_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.LAUNCHING_POWER_PER_RANK * level) - Spellbound.config.LAUNCHING_BASE_POWER;
        if(level > Spellbound.config.LAUNCHING_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.LAUNCHING_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.LAUNCHING_HARD_CAP;
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
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() == Items.BOOK;
    }
}
