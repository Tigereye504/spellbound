package net.tigereye.spellbound;

import net.minecraft.item.ItemStack;

public interface SpellboundProjectileEntity {
    ItemStack getSource();
    void setSource(ItemStack Source);
}
