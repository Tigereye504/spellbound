package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class LaunchingEnchantment extends SBEnchantment{

    public LaunchingEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE,
            Spellbound.config.airline.WEIGHT, //enchantment weight
            Spellbound.config.airline.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.airline.BASE_POWER,Spellbound.config.airline.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.airline.BASE_POWER+Spellbound.config.airline.POWER_RANGE,Spellbound.config.airline.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.airline.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.launching.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.launching.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.launching.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.launching.IS_FOR_SALE;}

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if(user instanceof SpellboundPlayerEntity &&
                !(((SpellboundPlayerEntity)user).spellbound$isMakingFullChargeAttack())){
            return;
        }
        target.setDeltaMovement(target.getDeltaMovement().x,Math.abs(target.getDeltaMovement().y)+(level*.3),target.getDeltaMovement().z);
        super.doPostAttack(user, target, level);
    }
}
