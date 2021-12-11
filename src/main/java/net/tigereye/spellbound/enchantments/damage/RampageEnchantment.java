package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class RampageEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public RampageEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.RAMPAGE_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (10 * level);
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 15;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 3;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        StatusEffectInstance greenSparkles = attacker.getStatusEffect(SBStatusEffects.GREEN_SPARKLES);
        if(greenSparkles != null){
            return Spellbound.config.RAMPAGE_DAMAGE_BASE + (Spellbound.config.RAMPAGE_DAMAGE_PER_LEVEL * level);
        }
        return 0;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            StatusEffectInstance greenSparkles = ((LivingEntity)attacker).getStatusEffect(SBStatusEffects.GREEN_SPARKLES);
            if (greenSparkles != null) {
                return damage + Spellbound.config.RAMPAGE_DAMAGE_BASE + (Spellbound.config.RAMPAGE_DAMAGE_PER_LEVEL * level);
            }
        }
        return damage;
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        killer.addStatusEffect(new StatusEffectInstance(SBStatusEffects.GREEN_SPARKLES,
                Spellbound.config.RAMPAGE_DURATION_BASE +(Spellbound.config.RAMPAGE_DURATION_PER_LEVEL*level),
                level-1));
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
