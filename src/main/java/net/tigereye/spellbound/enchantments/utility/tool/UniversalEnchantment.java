package net.tigereye.spellbound.enchantments.utility.tool;

import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;


public class UniversalEnchantment extends SBEnchantment{
    static int PRIORITY = 1;
    public UniversalEnchantment() {
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
    public boolean isEnabled() {return Spellbound.config.universal.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.universal.SOFT_CAP;}
    @Override
    public int getPriority(){return PRIORITY;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.universal.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.universal.IS_FOR_SALE;}
    @Override

    public float getBaseMiningSpeed(int level, ItemStack itemStack, BlockState block, Float miningSpeed) {
        if(miningSpeed > 1.0F){
            return miningSpeed;
        }
        Tool tool = itemStack.get(DataComponents.TOOL);
        if(tool == null){
            return miningSpeed;
        }
        miningSpeed = Math.max(miningSpeed,tool.getMiningSpeed(Blocks.STONE.defaultBlockState()));
        miningSpeed = Math.max(miningSpeed,tool.getMiningSpeed(Blocks.DIRT.defaultBlockState()));
        miningSpeed = Math.max(miningSpeed,tool.getMiningSpeed(Blocks.OAK_LOG.defaultBlockState()));
        miningSpeed = Math.max(miningSpeed,tool.getMiningSpeed(Blocks.OAK_LEAVES.defaultBlockState()));
        miningSpeed = Math.max(miningSpeed,tool.getMiningSpeed(Blocks.COBWEB.defaultBlockState()));
        return miningSpeed*Spellbound.config.universal.OFF_TYPE_MINING_SPEED_FACTOR;
    }

    @Override
    public boolean setItemSuitability(int level, ItemStack stack, BlockState state, Boolean suitability) {
        return true;
    }
}
