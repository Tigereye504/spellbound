package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.util.SpellboundUtil;

public class ImpersonalEnchantment extends SBEnchantment{

    public ImpersonalEnchantment() {
        //EnchantmentTarget is vanishable because I'm handling that myself and so want a very permissive filter
        super(SpellboundUtil.rarityLookup(Spellbound.config.impersonal.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.impersonal.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.impersonal.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.impersonal.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.impersonal.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.impersonal.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.impersonal.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.impersonal.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.impersonal.IS_FOR_SALE;}

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(user instanceof SpellboundPlayerEntity &&
                !(((SpellboundPlayerEntity)user).isMakingFullChargeAttack())){
            return;
        }
        if(user.hasVehicle()){
            user.stopRiding();
        }
        Direction shift = target.getHorizontalFacing().getOpposite();
        double distanceBehind = 3+target.getBoundingBox().getZLength();
        BlockPos newPos = BlockPos.ofFloored((target.getX() + (shift.getOffsetX()*distanceBehind)),
                (target.getY() + (shift.getOffsetY()*distanceBehind)),
                (target.getZ() + (shift.getOffsetZ()*distanceBehind)));
        BlockState newPosBlock = user.getWorld().getBlockState(newPos);
        if(!newPosBlock.isOpaque()) {
            user.teleport(newPos.getX(),newPos.getY(),newPos.getZ());
            user.setYaw(target.getHorizontalFacing().asRotation());
        }
        else{
            newPos = newPos.add(0,1,0);
            newPosBlock = user.getWorld().getBlockState(newPos);
            if(!newPosBlock.isOpaque()) {
                user.teleport(newPos.getX(),newPos.getY(),newPos.getZ());
                user.setYaw(target.getHorizontalFacing().asRotation());
            }
        }
        //TODO: insert warp sound effect here
        super.onTargetDamaged(user, target, level);
    }
}
