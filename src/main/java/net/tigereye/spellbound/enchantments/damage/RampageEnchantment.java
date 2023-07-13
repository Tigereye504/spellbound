package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RampageEnchantment extends SBEnchantment{

    public RampageEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.rampage.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.rampage.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.rampage.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.rampage.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.rampage.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.rampage.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.rampage.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.rampage.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.rampage.IS_FOR_SALE;}

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        StatusEffectInstance greenSparkles = attacker.getStatusEffect(SBStatusEffects.GREEN_SPARKLES);
        if(greenSparkles != null){
            return Spellbound.config.rampage.DAMAGE_BASE + (Spellbound.config.rampage.DAMAGE_PER_LEVEL * level);
        }
        return 0;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            StatusEffectInstance greenSparkles = ((LivingEntity)attacker).getStatusEffect(SBStatusEffects.GREEN_SPARKLES);
            if (greenSparkles != null) {
                return damage + Spellbound.config.rampage.DAMAGE_BASE + (Spellbound.config.rampage.DAMAGE_PER_LEVEL * level);
            }
        }
        return damage;
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        killer.addStatusEffect(new StatusEffectInstance(SBStatusEffects.GREEN_SPARKLES,
                Spellbound.config.rampage.DURATION_BASE +(Spellbound.config.rampage.DURATION_PER_LEVEL*level),
                level-1));
    }
}
