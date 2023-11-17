package net.tigereye.spellbound.items;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.efficiency.RockCollectingEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BagOfRocks extends Item {

    public BagOfRocks(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!user.getWorld().isClient()) {
            ItemStack bagOfRocks = user.getStackInHand(hand);
            for (ItemStack item : user.getHandItems()) {
                if (item != bagOfRocks && EnchantmentHelper.getLevel(SBEnchantments.ROCK_COLLECTING, item) > 0) {
                    Map<String,Integer> keyIntMap = SBEnchantments.ROCK_COLLECTING.getRockMap(bagOfRocks);
                    NbtCompound tag = item.getOrCreateSubNbt(RockCollectingEnchantment.ROCK_COLLECTOR_KEY);
                    for(Map.Entry<String,Integer> entry : keyIntMap.entrySet()){
                        tag.putInt(entry.getKey(),tag.getInt(entry.getKey()) + entry.getValue());
                    }
                    tag.putInt(RockCollectingEnchantment.UNIQUE_ROCK_COUNT_KEY,
                            tag.contains(RockCollectingEnchantment.UNIQUE_ROCK_COUNT_KEY) ?
                                    tag.getKeys().size() - 1 : tag.getKeys().size());
                    user.getStackInHand(hand).decrement(1);
                    return TypedActionResult.consume(user.getStackInHand(hand));
                }
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }

    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (world != null) {
            tooltip.addAll(SBEnchantments.ROCK_COLLECTING.addTooltip(stack, world));
        }
    }
}
