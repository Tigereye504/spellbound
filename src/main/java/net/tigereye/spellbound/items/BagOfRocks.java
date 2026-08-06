package net.tigereye.spellbound.items;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.components.RockCollectionComponent;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBEnchantments;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
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
            //If the bag doesn't have a rock collection component, just destroy the bag.
            if(!bagOfRocks.has(SBComponents.ROCK_COLECTION)){
                user.getItemInHand(hand).shrink(1);
                return InteractionResultHolder.consume(user.getItemInHand(hand));
            }
            for (ItemStack item : user.getHandSlots()) {
                if (item != bagOfRocks && EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.ROCK_COLLECTING, item) > 0) {
                    RockCollectionComponent baggedRocks = bagOfRocks.get(SBComponents.ROCK_COLECTION);
                    //if the target doesn't have a rock collection component, give it the bag's collection.
                    if(!item.has(SBComponents.ROCK_COLECTION)){
                        item.set(SBComponents.ROCK_COLECTION, baggedRocks);
                        user.getItemInHand(hand).shrink(1);
                        return InteractionResultHolder.consume(user.getItemInHand(hand));
                    }
                    //otherwise, combine the collections.
                    RockCollectionComponent itemsRocks = item.get(SBComponents.ROCK_COLECTION);
                    Map<Holder<Block>,Integer> itemRocksMap = new HashMap<>();
                    itemsRocks.rocks().forEach((entry) -> itemRocksMap.put(entry.block(), entry.count()));
                    for(RockCollectionComponent.Entry entry : baggedRocks.rocks()){
                        itemRocksMap.put(entry.block(), itemRocksMap.getOrDefault(entry.block(),0)+entry.count());
                    }
                    item.set(SBComponents.ROCK_COLECTION, RockCollectionComponent.ofMap(itemRocksMap));
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
