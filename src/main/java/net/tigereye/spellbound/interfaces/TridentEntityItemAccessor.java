package net.tigereye.spellbound.interfaces;

import net.minecraft.world.item.ItemStack;

public interface TridentEntityItemAccessor {
    ItemStack spellbound_getTridentStack();
    void spellbound_setTridentStack(ItemStack tridentStack);
}
