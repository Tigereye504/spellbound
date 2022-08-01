package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.SBStatusEffect;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PrimingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public PrimingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.PRIMING_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.PRIMING_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level*10);
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 5;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        //TODO: changed primed to use a psudo-explosion like outburst does
        if(target instanceof LivingEntity) {
            int effectLevel = 0;
            StatusEffectInstance primedInstance = ((LivingEntity)target).getStatusEffect(SBStatusEffects.PRIMED);
            if(primedInstance != null){
                effectLevel = Math.min(primedInstance.getAmplifier()+1, level-1);
            }
            ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(SBStatusEffects.PRIMED, 60, effectLevel));
        }

        super.onTargetDamaged(user, target, level);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() == Items.BOOK;
    }
}
