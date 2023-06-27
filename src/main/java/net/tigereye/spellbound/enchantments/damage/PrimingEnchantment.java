package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PrimingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public PrimingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.priming.RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.priming.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.priming.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.priming.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.priming.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.priming.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.priming.POWER_RANGE;}
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
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
