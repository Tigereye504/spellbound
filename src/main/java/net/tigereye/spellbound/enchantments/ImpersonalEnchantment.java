package net.tigereye.spellbound.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.tigereye.spellbound.SpellboundPlayerEntity;

public class ImpersonalEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public ImpersonalEnchantment() {
        //EnchantmentTarget is vanishable because I'm handling that myself and so want a very permissive filter
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
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
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
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
