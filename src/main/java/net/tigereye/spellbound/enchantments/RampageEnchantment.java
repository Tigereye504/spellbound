package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class RampageEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public RampageEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 5 + (10 * level);
    }

    public int getMaxPower(int level) {
        return super.getMinPower(level) + 15;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        StatusEffectInstance greenSparkles = attacker.getStatusEffect(SBStatusEffects.GREEN_SPARKLES);
        if(greenSparkles != null){
            return SBConfig.RAMPAGE_DAMAGE_BASE + (SBConfig.RAMPAGE_DAMAGE_PER_LEVEL * level);
        }
        return 0;
    }

    public void onKill(int level, ItemStack stack, LivingEntity killer, LivingEntity victim){
        killer.applyStatusEffect(new StatusEffectInstance(SBStatusEffects.GREEN_SPARKLES,
                SBConfig.RAMPAGE_DURATION_BASE +(SBConfig.RAMPAGE_DURATION_PER_LEVEL*level),
                level-1));
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.SHARPNESS);
    }

    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                //|| stack.getItem() instanceof BowItem
                //|| stack.getItem() instanceof CrossbowItem
                || stack.getItem() == Items.BOOK;
                //TODO: make rampage damage work for bows/crossbows
    }
}
