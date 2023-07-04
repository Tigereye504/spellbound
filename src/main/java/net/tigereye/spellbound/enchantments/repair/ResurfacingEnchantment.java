package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.ResurfacingItemsPersistentState;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class ResurfacingEnchantment extends SBEnchantment {

    public ResurfacingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.resurfacing.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.resurfacing.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.resurfacing.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.resurfacing.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.resurfacing.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.resurfacing.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.resurfacing.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.resurfacing.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.resurfacing.IS_FOR_SALE;}

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onToolBreak(int level, ItemStack itemStack, Entity entity) {
        if(entity.getWorld().isClient()){
            return;
        }
        MinecraftServer server = entity.getServer();
        if(server != null) {
            ItemStack copiedItemStack = itemStack.copy();
            copiedItemStack.setDamage(0);
            ResurfacingItemsPersistentState ripState = ResurfacingItemsPersistentState.getResurfacingItemsPersistentState(server);
            ripState.PushItem(copiedItemStack);
        }
    }
}
