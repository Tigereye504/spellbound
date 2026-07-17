package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class AccelerationEnchantment extends SBEnchantment{

    private static final String ACCELERATION_STACKS_KEY = Spellbound.MODID+"SB_Acceleration_Stacks";
    private static final String ACCELERATION_TIME_KEY = Spellbound.MODID+"SB_Acceleration_Time";
    public AccelerationEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.acceleration.RARITY), EnchantmentCategory.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.acceleration.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.acceleration.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.acceleration.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.acceleration.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.acceleration.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.acceleration.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.acceleration.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.acceleration.IS_FOR_SALE;}
    @Override
    public float getMiningSpeed(int level, Player playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        CompoundTag tag = stack.getOrCreateTag();
        float accelerationStacks = tag.getFloat(ACCELERATION_STACKS_KEY);
        if(accelerationStacks == 0 || !stack.isCorrectToolForDrops(block)) {
            return miningSpeed;
        }
        return miningSpeed + (Math.min(Spellbound.config.acceleration.MAX_ACCELERATION_STACKS,accelerationStacks)*level*level/10f);
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        CompoundTag tag = stack.getOrCreateTag();
        float accelerationStacks = tag.getFloat(ACCELERATION_STACKS_KEY);
        tag.putFloat(ACCELERATION_STACKS_KEY,accelerationStacks + state.getBlock().defaultDestroyTime());
        tag.putLong(ACCELERATION_TIME_KEY,world.getGameTime());
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info("Mining Speed: "+(accelerationStacks*level*level/10f));
            Spellbound.LOGGER.info("Acceleration Stacks: "+accelerationStacks);
        }
    }

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        CompoundTag tag = stack.getOrCreateTag();
        if(tag.contains(ACCELERATION_TIME_KEY)){
            long time = tag.getLong(ACCELERATION_TIME_KEY);
            if(entity.level().getGameTime() - time > Spellbound.config.acceleration.TIMEOUT){
                if(entity.swinging){
                    if (Spellbound.DEBUG){
                        Spellbound.LOGGER.info("Acceleration in overtime");
                    }
                }
                else {
                    tag.remove(ACCELERATION_TIME_KEY);
                    tag.remove(ACCELERATION_STACKS_KEY);
                    if (Spellbound.DEBUG) {
                        Spellbound.LOGGER.info("Acceleration Stacks Lost");
                    }
                }
            }
        }
    }


}
