package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class ImpersonalEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public ImpersonalEnchantment() {
        //EnchantmentTarget is vanishable because I'm handling that myself and so want a very permissive filter
        super(SpellboundUtil.rarityLookup(Spellbound.config.IMPERSONAL_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.IMPERSONAL_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.IMPERSONAL_POWER_PER_RANK * level) + Spellbound.config.IMPERSONAL_BASE_POWER;
        if(level > Spellbound.config.IMPERSONAL_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.IMPERSONAL_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.IMPERSONAL_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

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
        BlockPos newPos = new BlockPos(target.getX() + (shift.getOffsetX()*distanceBehind),
                target.getY() + (shift.getOffsetY()*distanceBehind),
                target.getZ() + (shift.getOffsetZ()*distanceBehind));
        BlockState newPosBlock = user.world.getBlockState(newPos);
        if(!newPosBlock.isOpaque()) {
            user.teleport(newPos.getX(),newPos.getY(),newPos.getZ());
            user.setYaw(target.getHorizontalFacing().asRotation());
        }
        else{
            newPos = newPos.add(0,1,0);
            newPosBlock = user.world.getBlockState(newPos);
            if(!newPosBlock.isOpaque()) {
                user.teleport(newPos.getX(),newPos.getY(),newPos.getZ());
                user.setYaw(target.getHorizontalFacing().asRotation());
            }
        }
        //TODO: insert warp sound effect here
        super.onTargetDamaged(user, target, level);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() == Items.BOOK;
    }
}
