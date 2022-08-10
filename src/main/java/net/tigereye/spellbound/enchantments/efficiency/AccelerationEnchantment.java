package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class AccelerationEnchantment extends SBEnchantment{

    private static final String ACCELERATION_STACKS_KEY = Spellbound.MODID+"SB_Acceleration_Stacks";
    private static final String ACCELERATION_TIME_KEY = Spellbound.MODID+"SB_Acceleration_Time";
    public AccelerationEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.ACCELERATION_RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.ACCELERATION_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        return 1 + 10 * (level - 1);
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 5;
        else return 0;
    }

    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        NbtCompound tag = stack.getOrCreateNbt();
        float accelerationStacks = tag.getFloat(ACCELERATION_STACKS_KEY);
        if(accelerationStacks == 0 || !stack.isSuitableFor(block)) {
            return miningSpeed;
        }
        return miningSpeed + (accelerationStacks*level*level/10f);
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        NbtCompound tag = stack.getOrCreateNbt();
        float accelerationStacks = tag.getFloat(ACCELERATION_STACKS_KEY);
        tag.putFloat(ACCELERATION_STACKS_KEY,accelerationStacks + state.getBlock().getHardness());
        tag.putLong(ACCELERATION_TIME_KEY,world.getTime());
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info("Mining Speed: "+(accelerationStacks*level*level/10f));
            Spellbound.LOGGER.info("Acceleration Stacks: "+accelerationStacks);
        }
    }

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        NbtCompound tag = stack.getOrCreateNbt();
        if(tag.contains(ACCELERATION_TIME_KEY)){
            long time = tag.getLong(ACCELERATION_TIME_KEY);
            if(entity.getWorld().getTime() - time > Spellbound.config.ACCELERATION_TIMEOUT){
                if(entity.handSwinging){
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

    @Override
    public boolean isTreasure() {
        return false;
    }
}
