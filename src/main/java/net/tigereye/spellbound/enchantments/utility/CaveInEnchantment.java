package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.block.BlockState;
import net.minecraft.block.FallingBlock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.config.SBConfig;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.List;

public class CaveInEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public CaveInEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.caveIn.RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.caveIn.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.caveIn.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.caveIn.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.caveIn.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.caveIn.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.caveIn.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.caveIn.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.caveIn.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() == Items.BOOK;
    }

    @Override
    public void onProjectileBlockHit(int level, ItemStack itemStack, ProjectileEntity projectileEntity, BlockHitResult blockHitResult) {
        caveIn(level,projectileEntity.world,blockHitResult.getBlockPos());
    }

    private void caveIn(int level, World world, BlockPos center){
        BlockPos lowerCorner = center.add(1-level,1-level,1-level);
        List<BlockState> fallingBlocks = new ArrayList<>();
        int size = (level*2)-1;
        BlockPos target;
        BlockState targetBlock;
        BlockState blockBelowTarget;
        for(int y = 0; y < size; y++){
            if(lowerCorner.getY()+y >= world.getBottomY() && lowerCorner.getY()+y <= world.getTopY()){
                for(int x = 0; x < size; x++){
                    for(int z = 0; z < size; z++){
                        target = lowerCorner.add(x,y,z);
                        targetBlock = world.getBlockState(target);
                        blockBelowTarget = world.getBlockState(target.down());
                        if(!targetBlock.isAir() &&
                                (targetBlock.getBlock().getBlastResistance() <= Spellbound.config.caveIn.MAX_BLAST_RES || Spellbound.config.UNLIMITED_CAVE_IN)
                                && world.getBlockEntity(target) == null
                                && (fallingBlocks.contains(blockBelowTarget) || FallingBlock.canFallThrough(blockBelowTarget))){
                            FallingBlockEntity.spawnFromBlock(world,target,targetBlock);
                            fallingBlocks.add(targetBlock);
                        }
                    }
                }
            }
        }
    }
}
