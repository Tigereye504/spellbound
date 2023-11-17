package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.NextTickAction;
import net.tigereye.spellbound.interfaces.SpellboundExplosion;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;

public class DemolitionEnchantment extends SBEnchantment {

    public static final String DEMOLTION_LAST_BLAST_KEY = Spellbound.MODID+"Demolition_Last_Blast";
    public DemolitionEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.demolition.RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
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
    public boolean isTreasure() {return Spellbound.config.demolition.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.demolition.IS_FOR_SALE;}

    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        float hardness = block.getBlock().getHardness();
        if(hardness == 0){
            return miningSpeed;
        }
        return Math.min(miningSpeed,hardness*1.5f);
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.getBlock().getHardness() == 0){
            return;
        }
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        long time = nbtCompound.getLong(DEMOLTION_LAST_BLAST_KEY);
        if(world.getTime() - time > 2){
            nbtCompound.putLong(DEMOLTION_LAST_BLAST_KEY,world.getTime());
            ((SpellboundLivingEntity)player).spellbound$addNextTickAction(new DemolitionAction(world, player, pos,
                    Spellbound.config.demolition.BASE_EXPLOSION_POWER + (Spellbound.config.demolition.EXPLOSION_POWER_PER_RANK *level)));

        }
        else if(player instanceof ServerPlayerEntity) {
            stack.damage(1, player.getRandom(), (ServerPlayerEntity) player);
        }
    }

    private static class DemolitionAction implements NextTickAction{

        World world;
        PlayerEntity player;
        BlockPos pos;
        float power;

        DemolitionAction(World world, PlayerEntity playerEntity, BlockPos pos, float power){
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
            if(!world.isClient()) {
                Explosion explosion = new Explosion(world, player, null, null, x, y, z, power, false, Explosion.DestructionType.DESTROY);
                ((SpellboundExplosion) explosion).collectBlocksAndDamageNonItemEntities();
                List<BlockPos> explodedBlocks = explosion.getAffectedBlocks();
                for (BlockPos position : explodedBlocks) {
                    BlockState state = world.getBlockState(position);
                    SBEnchantmentHelper.onBreakBlock(state.getBlock(), world, position, state, player);
                }
                explosion.affectWorld(true);
            }
            else{
                this.world.playSound(x, y, z, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F, false);

                if (!(this.power < 2.0F)) {
                    this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, x, y, z, 1.0D, 0.0D, 0.0D);
                } else {
                    this.world.addParticle(ParticleTypes.EXPLOSION, x, y, z, 1.0D, 0.0D, 0.0D);
                }
            }
        }
    }
}
