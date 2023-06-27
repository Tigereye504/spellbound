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
        super(SpellboundUtil.rarityLookup(Spellbound.config.launching.RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.launching.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.launching.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.launching.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.launching.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.launching.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.launching.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.launching.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.launching.IS_FOR_SALE;}
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
