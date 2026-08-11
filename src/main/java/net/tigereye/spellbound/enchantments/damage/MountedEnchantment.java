package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class MountedEnchantment extends SBEnchantment{

    public MountedEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE, //enchantment targets: weapons
            Spellbound.config.mounted.WEIGHT, //enchantment weight
            Spellbound.config.mounted.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.mounted.BASE_POWER,Spellbound.config.mounted.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.mounted.BASE_POWER+Spellbound.config.mounted.POWER_RANGE,Spellbound.config.mounted.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.mounted.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.mounted.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.mounted.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.mounted.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.mounted.IS_FOR_SALE;}

    @Override
    public float getDamageBonus(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        if(attacker.isPassenger()){
            return (Spellbound.config.mounted.DAMAGE_PER_LEVEL * level) + Spellbound.config.mounted.DAMAGE_BASE;
        }
        return 0;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, AbstractArrow projectile, Entity attacker, Entity defender, float damage) {
        if(attacker.getVehicle() != null){
            return damage * ((Spellbound.config.mounted.PROJECTILE_PER_LEVEL * level) + Spellbound.config.mounted.PROJECTILE_BASE);
        }
        return damage;
    }
}
