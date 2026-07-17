package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundExplosion;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;

public class DemolitionEnchantment extends SBEnchantment {

    public DemolitionEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.demolition.RARITY), EnchantmentCategory.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.demolition.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.demolition.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.demolition.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.demolition.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.demolition.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.demolition.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.demolition.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.demolition.IS_FOR_SALE;}

    @Override
    public float getMiningSpeed(int level, Player playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        float hardness = block.getBlock().defaultDestroyTime();
        if(hardness == 0){
            return miningSpeed;
        }
        return Math.min(miningSpeed,hardness*1.5f);
    }

    @Override
    public void onBreakBlockDirectly(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if(state.getBlock().defaultDestroyTime() == 0){
            return;
        }
        ((SpellboundLivingEntity)player).spellbound$addDelayedAction(new DemolitionAction(world, player, pos,
            Spellbound.config.demolition.BASE_EXPLOSION_POWER + (Spellbound.config.demolition.EXPLOSION_POWER_PER_RANK *level)));
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if(state.getBlock().defaultDestroyTime() == 0){
            return;
        }
        stack.mineBlock(world,state,pos,player);
    }

    private static class DemolitionAction extends DelayedAction {

        Level world;
        Player player;
        BlockPos pos;
        float power;

        DemolitionAction(Level world, Player playerEntity, BlockPos pos, float power){
            this.world = world;
            this.player = playerEntity;
            this.pos = pos;
            this.power = power;
        }
        @Override
        public void act() {
            double x = pos.getX() + .5;
            double y = pos.getY() + .5;
            double z = pos.getZ() + .5;
            if(!world.isClientSide()) {
                Explosion explosion = new Explosion(world, player, null, null, x, y, z, power, false, Explosion.BlockInteraction.DESTROY);
                ((SpellboundExplosion) explosion).collectBlocksAndDamageNonItemEntities();
                List<BlockPos> explodedBlocks = explosion.getToBlow();
                for (BlockPos position : explodedBlocks) {
                    BlockState state = world.getBlockState(position);
                    SBEnchantmentHelper.onBreakBlock(state.getBlock(), world, position, state, player);
                }
                explosion.finalizeExplosion(true);
            }
            else{
                this.world.playLocalSound(x, y, z, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);

                if (!(this.power < 2.0F)) {
                    this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0D, 0.0D, 0.0D);
                } else {
                    this.world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}
