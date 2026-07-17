package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.ResurfacingItemsPersistentState;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class ResurfacingEnchantment extends SBEnchantment {

    public ResurfacingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.resurfacing.RARITY), EnchantmentCategory.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
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
    public boolean isTreasureOnly() {return Spellbound.config.resurfacing.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.resurfacing.IS_FOR_SALE;}

    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public void onToolBreak(int level, ItemStack itemStack, Entity entity) {
        if(entity.level().isClientSide()){
            return;
        }
        MinecraftServer server = entity.getServer();
        if(server != null) {
            ItemStack copiedItemStack = itemStack.copy();
            copiedItemStack.setDamageValue(0);
            ResurfacingItemsPersistentState ripState = ResurfacingItemsPersistentState.getResurfacingItemsPersistentState(server);
            ripState.PushItem(copiedItemStack);
        }
    }
}
