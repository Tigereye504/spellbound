package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class AirlineEnchantment extends SBEnchantment{

    public AirlineEnchantment() {
        super(definition(SBTags.RANGED_WEAPONS_ENCHANTABLE,
            Spellbound.config.airline.WEIGHT, //enchantment weight
            Spellbound.config.airline.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.airline.BASE_POWER,Spellbound.config.airline.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.airline.BASE_POWER+Spellbound.config.airline.POWER_RANGE,Spellbound.config.airline.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.airline.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.airline.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.airline.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.airline.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.airline.IS_FOR_SALE;}

    @Override
    public void onFireProjectile(int level, ItemStack itemStack, Entity entity, Projectile projectile){
        //TODO: require user to be grounded
        if(entity instanceof LivingEntity livingEntity){
            tetherTarget(level,projectile,livingEntity);
        }
    }

    @Override
    public void onThrowTrident(int level, ItemStack itemStack, Entity entity, ThrownTrident projectile){
        if(entity instanceof LivingEntity livingEntity){
            tetherTarget(level,projectile,livingEntity);
        }
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        target.removeEffect(SBStatusEffects.TETHERED);
        ((SpellboundLivingEntity)target).spellbound$setLastTether(anchor.getUUID());
        target.addEffect(new MobEffectInstance(SBStatusEffects.TETHERED, Spellbound.config.airline.BASE_DURATION + (Spellbound.config.airline.DURATION_PER_RANK*level), 0));
    }
}
