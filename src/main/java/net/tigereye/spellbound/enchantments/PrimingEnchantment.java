package net.tigereye.spellbound.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.tigereye.spellbound.mob_effect.SBStatusEffect;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class PrimingEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public PrimingEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) { return (level*15); }

    public int getMaxPower(int level) { return getMinPower(level)+15; }

    public int getMaxLevel() {
        return 10;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

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

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }

    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() == Items.BOOK;
                //TODO: Implement for Bow/Crossbow
    }
}
