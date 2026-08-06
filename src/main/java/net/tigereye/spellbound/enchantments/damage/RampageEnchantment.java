package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RampageEnchantment extends SBEnchantment{

    public RampageEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE, //enchantment targets: ALL weapons, both melee and ranged
            SpellboundUtil.rarityLookup(Spellbound.config.rampage.RARITY), //enchantment weight
            Spellbound.config.rampage.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.rampage.BASE_POWER,Spellbound.config.rampage.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.rampage.BASE_POWER+Spellbound.config.rampage.POWER_RANGE,Spellbound.config.rampage.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.rampage.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.rampage.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.rampage.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.rampage.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.rampage.IS_FOR_SALE;}

    @Override
    public float getDamageBonus(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        MobEffectInstance greenSparkles = attacker.getEffect(SBStatusEffects.GREEN_SPARKLES);
        if(greenSparkles != null){
            return Spellbound.config.rampage.DAMAGE_BASE + (Spellbound.config.rampage.DAMAGE_PER_LEVEL * level);
        }
        return 0;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, AbstractArrow projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            MobEffectInstance greenSparkles = ((LivingEntity)attacker).getEffect(SBStatusEffects.GREEN_SPARKLES);
            if (greenSparkles != null) {
                return damage + Spellbound.config.rampage.DAMAGE_BASE + (Spellbound.config.rampage.DAMAGE_PER_LEVEL * level);
            }
        }
        return damage;
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        killer.addEffect(new MobEffectInstance(SBStatusEffects.GREEN_SPARKLES,
                Spellbound.config.rampage.DURATION_BASE +(Spellbound.config.rampage.DURATION_PER_LEVEL*level),
                level-1));
    }
}
