package net.tigereye.spellbound.enchantments.utility.chestplate;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class RepulsiveEnchantment extends SBEnchantment{


    public RepulsiveEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.REPULSIVE_RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.REPULSIVE_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.REPULSIVE_POWER_PER_RANK * level) + Spellbound.config.REPULSIVE_BASE_POWER;
        if(level > Spellbound.config.REPULSIVE_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.REPULSIVE_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.REPULSIVE_HARD_CAP;
        else return 0;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.getEntityWorld();
        if(!world.isClient()){
            if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
                return;
            }
            if(stack == entity.getEquippedStack(EquipmentSlot.CHEST)) {
                SpellboundUtil.pushPullEntitiesPlayersInRange(Spellbound.config.ATTRACTION_RANGE, -Spellbound.config.ATTRACTION_STRENGTH, entity);
            }
        }
    }
}
