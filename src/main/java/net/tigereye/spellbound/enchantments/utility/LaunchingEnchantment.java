package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
    public boolean isTreasureOnly() {return Spellbound.config.launching.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.launching.IS_FOR_SALE;}

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if(user instanceof SpellboundPlayerEntity &&
                !(((SpellboundPlayerEntity)user).isMakingFullChargeAttack())){
            return;
        }
        target.setDeltaMovement(target.getDeltaMovement().x,Math.abs(target.getDeltaMovement().y)+(level*.3),target.getDeltaMovement().z);
        super.doPostAttack(user, target, level);
    }
}
