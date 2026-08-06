package net.tigereye.spellbound.enchantments.fortune;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.Prospector.ProspectorManager;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.Map;

public class ProspectorEnchantment extends SBEnchantment {

    public ProspectorEnchantment() {
        super(definition(ItemTags.MINING_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.prospector.RARITY), //enchantment weight
            Spellbound.config.prospector.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.prospector.BASE_POWER,Spellbound.config.prospector.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.prospector.BASE_POWER+Spellbound.config.prospector.POWER_RANGE,Spellbound.config.prospector.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.prospector.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.prospector.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.prospector.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.prospector.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.prospector.IS_FOR_SALE;}
    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if(world.isClientSide()){
            return;
        }
        if(state.getBlock().defaultDestroyTime() == 0){
            return;
        }
        if(world instanceof ServerLevel sWorld) {
            if (ProspectorManager.detectTouchedBlock(sWorld, pos)) {
                return;
            }
            Map<ResourceLocation, Float> rates = ProspectorManager.getDropRateMapWithBonuses(sWorld, pos, Spellbound.config.prospector.RADIUS);
            RandomSource random = player.getRandom();
            for (Map.Entry<ResourceLocation, Float> entry : rates.entrySet()) {
                if (entry.getValue() > 0) {
                    Item treasure = BuiltInRegistries.ITEM.get(entry.getKey());
                    if (treasure != Items.AIR) {
                        if (Spellbound.DEBUG) {
                            Spellbound.LOGGER.info("Prospecting " + Component.translatable(treasure.getDescriptionId()).getString() + ". Attempts: " + level + ". Odds: " + entry.getValue());
                        }
                        int count = 0;
                        for (int i = 0; i < level; i++) {
                            if (random.nextFloat() < entry.getValue()) {
                                count++;
                            }
                        }
                        ((SpellboundLivingEntity) player).spellbound$addDelayedAction(new ProspectorAction(sWorld, pos, new ItemStack(treasure, count)));
                    } else {
                        Spellbound.LOGGER.error(player.getName().getString() + "'s Prospector is looking for " + entry.getKey() + ", but cannot find it in the item registry!");
                    }
                }
            }
        }
    }

    private static class ProspectorAction extends DelayedAction {

        Level world;
        BlockPos pos;
        ItemStack stack;

        ProspectorAction(Level world, BlockPos pos, ItemStack stack){
            this.world = world;
            this.pos = pos;
            this.stack = stack;
        }
        @Override
        public void act() {
            world.addFreshEntity(new ItemEntity(world,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack));
        }
    }
}
