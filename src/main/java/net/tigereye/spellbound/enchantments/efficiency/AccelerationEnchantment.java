package net.tigereye.spellbound.enchantments.efficiency;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;


public class AccelerationEnchantment extends SBEnchantment{

    public static final String ACCELERATION_STACKS_KEY = Spellbound.MODID+"SB_Acceleration_Stacks";
    public static final String ACCELERATION_TIME_KEY = Spellbound.MODID+"SB_Acceleration_Time";

    public AccelerationEnchantment() {
        super(definition(ItemTags.MINING_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.acceleration.RARITY), //enchantment weight
            Spellbound.config.acceleration.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.acceleration.BASE_POWER,Spellbound.config.acceleration.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.acceleration.BASE_POWER+Spellbound.config.acceleration.POWER_RANGE,Spellbound.config.acceleration.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.acceleration.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.acceleration.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.acceleration.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.acceleration.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.acceleration.IS_FOR_SALE;}

    @Override
    public float getMiningSpeed(int level, Player playerEntity, ItemStack stack, BlockState block, float miningSpeed) {
        //if acceleration is stale, drop the combo and return
        if(checkForTimeout(stack, playerEntity)) return miningSpeed;
        //else get acceleration amount and reset the clock
        stack.set(SBComponents.ACCELERATION_TIME,playerEntity.getCommandSenderWorld().getGameTime());
        float accelerationStacks = stack.getOrDefault(SBComponents.ACCELERATION_STACKS,0f);
        if(accelerationStacks == 0 || !stack.isCorrectToolForDrops(block)) {
            return miningSpeed;
        }
        return miningSpeed + (accelerationStacks*level*level*Spellbound.config.acceleration.ACCELERATION_FACTOR);
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        float accelerationStacks = stack.getOrDefault(SBComponents.ACCELERATION_STACKS,0f);
        stack.set(SBComponents.ACCELERATION_STACKS,
            Math.min(Spellbound.config.acceleration.MAX_ACCELERATION_STACKS,accelerationStacks + state.getBlock().defaultDestroyTime()));
        stack.set(SBComponents.ACCELERATION_TIME,world.getGameTime());
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info("Mining Speed: "+(accelerationStacks*level*level/10f));
            Spellbound.LOGGER.info("Acceleration Stacks: "+accelerationStacks);
        }
    }

    private boolean checkForTimeout(ItemStack stack, LivingEntity entity){
        boolean timedOut = true;
        if(stack.has(SBComponents.ACCELERATION_TIME)){
            long time = stack.getOrDefault(SBComponents.ACCELERATION_TIME,0l);
            timedOut = entity.level().getGameTime() - time > Spellbound.config.acceleration.TIMEOUT;
        }
        if(timedOut){
            stack.remove(SBComponents.ACCELERATION_TIME);
            stack.remove(SBComponents.ACCELERATION_STACKS);
            if (Spellbound.DEBUG) {
                Spellbound.LOGGER.info("Acceleration Stacks Lost");
            }
        }
        return timedOut;
    }


}
