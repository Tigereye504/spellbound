package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.util.SpellboundUtil;

public class LaunchingEnchantment extends SBEnchantment{

    public LaunchingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.launching.RARITY), SBEnchantmentTargets.RANGED_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.launching.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.launching.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.launching.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.launching.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.launching.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.launching.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.launching.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.launching.IS_FOR_SALE;}

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(user instanceof SpellboundPlayerEntity &&
                !(((SpellboundPlayerEntity)user).isMakingFullChargeAttack())){
            return;
        }
        target.setVelocity(target.getVelocity().x,Math.abs(target.getVelocity().y)+(level*.3),target.getVelocity().z);
        super.onTargetDamaged(user, target, level);
    }
}
