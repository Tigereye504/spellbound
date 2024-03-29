package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class AttractiveEnchantment extends SBEnchantment{

    public AttractiveEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.ATTRACTIVE_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 1;
        else return 0;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.getEntityWorld();
        if(!world.isClient()){
            if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
                return;
            }
            SpellboundUtil.pushPullEntitiesPlayersInRange(Spellbound.config.ATTRACTION_RANGE, Spellbound.config.ATTRACTION_STRENGTH, entity);
        }
    }
}
