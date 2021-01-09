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

import java.awt.print.Book;

public class ImpersonalEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public ImpersonalEnchantment() {
        //EnchantmentTarget is vanishable because I'm handling that myself and so want a very permissive filter
        super(Rarity.RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 20;
    }

    public int getMaxPower(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        Direction shift = target.getHorizontalFacing().getOpposite();
        double distanceBehind = 3+target.getBoundingBox().getZLength();
        BlockPos targetPos = new BlockPos(target.getX() + (shift.getOffsetX()*distanceBehind),
                target.getY() + (shift.getOffsetY()*distanceBehind),
                target.getZ() + (shift.getOffsetZ()*distanceBehind));
        BlockState targetBlock = user.world.getBlockState(targetPos);
        if(!targetBlock.isOpaque()) {
            user.teleport(targetPos.getX(),targetPos.getY(),targetPos.getZ());
        }
        else{
            targetPos = targetPos.add(0,1,0);
            targetBlock = user.world.getBlockState(targetPos);
            if(!targetBlock.isOpaque()) {
                user.teleport(targetPos.getX(),targetPos.getY(),targetPos.getZ());
            }
        }
        //TODO: insert warp sound effect here
        super.onTargetDamaged(user, target, level);
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }

    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() == Items.BOOK;
    }
}
