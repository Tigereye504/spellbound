package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.util.SpellboundUtil;

public class MountedEnchantment extends SBEnchantment{

    public MountedEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.mounted.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.mounted.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.mounted.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.mounted.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.mounted.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.mounted.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.mounted.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.mounted.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.mounted.IS_FOR_SALE;}

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        if(attacker.hasVehicle()){
            return (Spellbound.config.mounted.DAMAGE_PER_LEVEL * level) + Spellbound.config.mounted.DAMAGE_BASE;
        }
        return 0;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker.getVehicle() != null){
            return damage * ((Spellbound.config.mounted.PROJECTILE_PER_LEVEL * level) + Spellbound.config.mounted.PROJECTILE_BASE);
        }
        return damage;
    }
}
