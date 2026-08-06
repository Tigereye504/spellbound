package net.tigereye.spellbound.items;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.components.TrophyCollectionComponent;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBEnchantments;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BagOfTrophies extends Item {

    public BagOfTrophies(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        if (!user.level().isClientSide()) {
            ItemStack bagOfTrophies = user.getItemInHand(hand);
            //If the bag doesn't have a Trophy collection component, just destroy the bag.
            if(!bagOfTrophies.has(SBComponents.TROPHY_COLECTION)){
                user.getItemInHand(hand).shrink(1);
                return InteractionResultHolder.consume(user.getItemInHand(hand));
            }
            for (ItemStack item : user.getHandSlots()) {
                if (item != bagOfTrophies && EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.TROPHY_COLLECTING, item) > 0) {
                    TrophyCollectionComponent baggedTrophies = bagOfTrophies.get(SBComponents.TROPHY_COLECTION);
                    //if the target doesn't have a Trophy collection component, give it the bag's collection.
                    if(!item.has(SBComponents.TROPHY_COLECTION)){
                        item.set(SBComponents.TROPHY_COLECTION, baggedTrophies);
                        user.getItemInHand(hand).shrink(1);
                        return InteractionResultHolder.consume(user.getItemInHand(hand));
                    }
                    //otherwise, combine the collections.
                    TrophyCollectionComponent itemsTrophys = item.get(SBComponents.TROPHY_COLECTION);
                    Map<Holder<EntityType<?>>,Integer> itemTrophysMap = new HashMap<>();
                    itemsTrophys.trophies().forEach((entry) -> itemTrophysMap.put(entry.entityType(), entry.count()));
                    for(TrophyCollectionComponent.Entry entry : baggedTrophies.trophies()){
                        itemTrophysMap.put(entry.entityType(), itemTrophysMap.getOrDefault(entry.entityType(),0)+entry.count());
                    }
                    item.set(SBComponents.TROPHY_COLECTION, TrophyCollectionComponent.ofMap(itemTrophysMap));
                    user.getItemInHand(hand).shrink(1);
                    return InteractionResultHolder.consume(user.getItemInHand(hand));
                }
            }
        }
        return InteractionResultHolder.success(user.getItemInHand(hand));
    }

    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (world != null) {
            tooltip.addAll(SBEnchantments.TROPHY_COLLECTING.addTooltip(stack, world));
        }
    }
}
