package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.List;

public class CaveInEnchantment extends SBEnchantment{

    public CaveInEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.caveIn.RARITY), SBEnchantmentTargets.RANGED_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND},false);
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
    public boolean isTreasureOnly() {return Spellbound.config.caveIn.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.caveIn.IS_FOR_SALE;}

    @Override
    public void onProjectileBlockHit(int level, ItemStack itemStack, Projectile projectileEntity, BlockHitResult blockHitResult) {
        caveIn(level,projectileEntity.level(),blockHitResult.getBlockPos());
    }

    private void caveIn(int level, Level world, BlockPos center){
        BlockPos lowerCorner = center.offset(1-level,1-level,1-level);
        List<BlockState> fallingBlocks = new ArrayList<>();
        int size = (level*2)-1;
        BlockPos target;
        BlockState targetBlock;
        BlockState blockBelowTarget;
        for(int y = 0; y < size; y++){
            if(lowerCorner.getY()+y >= world.getMinBuildHeight() && lowerCorner.getY()+y <= world.getMaxBuildHeight()){
                for(int x = 0; x < size; x++){
                    for(int z = 0; z < size; z++){
                        target = lowerCorner.offset(x,y,z);
                        targetBlock = world.getBlockState(target);
                        blockBelowTarget = world.getBlockState(target.below());
                        if(!targetBlock.isAir() &&
                                (targetBlock.getBlock().getExplosionResistance() <= Spellbound.config.caveIn.MAX_BLAST_RES || Spellbound.config.UNLIMITED_CAVE_IN)
                                && world.getBlockEntity(target) == null
                                && (fallingBlocks.contains(blockBelowTarget) || FallingBlock.isFree(blockBelowTarget))){
                            FallingBlockEntity.fall(world,target,targetBlock);
                            fallingBlocks.add(targetBlock);
                        }
                    }
                }
            }
        }
    }
}
