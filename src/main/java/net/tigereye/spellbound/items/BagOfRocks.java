package net.tigereye.spellbound.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.enchantments.efficiency.RockCollectingEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class BagOfRocks extends Item {

    public BagOfRocks(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!user.level().isClientSide()) {
            ItemStack bagOfRocks = user.getItemInHand(hand);
            for (ItemStack item : user.getHandSlots()) {
                if (item != bagOfRocks && EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.ROCK_COLLECTING, item) > 0) {
                    Map<String,Integer> keyIntMap = SBEnchantments.ROCK_COLLECTING.getRockMap(bagOfRocks);
                    CompoundTag tag = item.getOrCreateTagElement(RockCollectingEnchantment.ROCK_COLLECTOR_KEY);
                    for(Map.Entry<String,Integer> entry : keyIntMap.entrySet()){
                        tag.putInt(entry.getKey(),tag.getInt(entry.getKey()) + entry.getValue());
                    }
                    tag.putInt(RockCollectingEnchantment.UNIQUE_ROCK_COUNT_KEY,
                            tag.contains(RockCollectingEnchantment.UNIQUE_ROCK_COUNT_KEY) ?
                                    tag.getAllKeys().size() - 1 : tag.getAllKeys().size());
                    user.getItemInHand(hand).shrink(1);
                    return InteractionResultHolder.consume(user.getItemInHand(hand));
                }
            }
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (world != null) {
            tooltip.addAll(SBEnchantments.ROCK_COLLECTING.addTooltip(stack, world));
        }
    }
}
