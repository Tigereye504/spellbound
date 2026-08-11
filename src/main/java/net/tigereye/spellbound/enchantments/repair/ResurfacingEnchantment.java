package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.ResurfacingItemsPersistentState;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class ResurfacingEnchantment extends SBEnchantment {

    public ResurfacingEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            Spellbound.config.resurfacing.WEIGHT, //enchantment weight
            Spellbound.config.resurfacing.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.resurfacing.BASE_POWER,Spellbound.config.resurfacing.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.resurfacing.BASE_POWER+Spellbound.config.resurfacing.POWER_RANGE,Spellbound.config.resurfacing.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.resurfacing.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.MAINHAND}),
            false); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.resurfacing.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.resurfacing.SOFT_CAP;}
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
