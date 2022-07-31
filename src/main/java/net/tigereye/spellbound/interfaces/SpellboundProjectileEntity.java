package net.tigereye.spellbound.interfaces;

import net.minecraft.item.ItemStack;

public interface SpellboundProjectileEntity {
    ItemStack getSource();
    void setSource(ItemStack Source);
}
