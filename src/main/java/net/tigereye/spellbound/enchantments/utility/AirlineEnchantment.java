package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
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
    public boolean isTreasure() {return Spellbound.config.airline.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.airline.IS_FOR_SALE;}

    @Override
    public void onFireProjectile(int level, ItemStack itemStack, Entity entity, ProjectileEntity projectile){
        //TODO: require user to be grounded
        if(entity instanceof LivingEntity livingEntity){
            tetherTarget(level,projectile,livingEntity);
        }
    }

    @Override
    public void onThrowTrident(int level, ItemStack itemStack, Entity entity, TridentEntity projectile){
        if(entity instanceof LivingEntity livingEntity){
            tetherTarget(level,projectile,livingEntity);
        }
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        target.removeStatusEffect(SBStatusEffects.TETHERED);
        target.addStatusEffect(new TetheredInstance(anchor, Spellbound.config.airline.BASE_DURATION + (Spellbound.config.airline.DURATION_PER_RANK*level), 0));
    }
}
