package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
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
    public boolean isTreasureOnly() {return Spellbound.config.impersonal.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.impersonal.IS_FOR_SALE;}

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        if(user instanceof SpellboundPlayerEntity &&
                !(((SpellboundPlayerEntity)user).isMakingFullChargeAttack())){
            return;
        }
        if(user.isPassenger()){
            user.stopRiding();
        }
        Direction shift = target.getDirection().getOpposite();
        double distanceBehind = 3+target.getBoundingBox().getZsize();
        BlockPos newPos = BlockPos.containing((target.getX() + (shift.getStepX()*distanceBehind)),
                (target.getY() + (shift.getStepY()*distanceBehind)),
                (target.getZ() + (shift.getStepZ()*distanceBehind)));
        BlockState newPosBlock = user.level().getBlockState(newPos);
        if(!newPosBlock.canOcclude()) {
            user.teleportToWithTicket(newPos.getX(),newPos.getY(),newPos.getZ());
            user.setYRot(target.getDirection().toYRot());
        }
        else{
            newPos = newPos.offset(0,1,0);
            newPosBlock = user.level().getBlockState(newPos);
            if(!newPosBlock.canOcclude()) {
                user.teleportToWithTicket(newPos.getX(),newPos.getY(),newPos.getZ());
                user.setYRot(target.getDirection().toYRot());
            }
        }
        //TODO: insert warp sound effect here
        super.doPostAttack(user, target, level);
    }
}
