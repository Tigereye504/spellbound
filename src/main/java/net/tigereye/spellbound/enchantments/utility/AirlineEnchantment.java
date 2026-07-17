package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class AirlineEnchantment extends SBEnchantment{

    public AirlineEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.airline.RARITY), SBEnchantmentTargets.RANGED_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.airline.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.airline.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.airline.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.airline.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.airline.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.airline.POWER_RANGE;}
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
        target.addEffect(new OwnedStatusEffectInstance(anchor, SBStatusEffects.TETHERED, Spellbound.config.airline.BASE_DURATION + (Spellbound.config.airline.DURATION_PER_RANK*level), 0));
    }
}
