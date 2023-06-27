package net.tigereye.spellbound.enchantments.utility.chestplate;

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
        super(SpellboundUtil.rarityLookup(Spellbound.config.repulsive.RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.repulsive.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.repulsive.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.repulsive.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.repulsive.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.repulsive.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.repulsive.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.repulsive.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.repulsive.IS_FOR_SALE;}
    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.getEntityWorld();
        if(!world.isClient()){
            if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
                return;
            }
            if(stack == entity.getEquippedStack(EquipmentSlot.CHEST)) {
                SpellboundUtil.pushPullEntitiesPlayersInRange(Spellbound.config.repulsive.RANGE, -Spellbound.config.repulsive.STRENGTH, entity);
            }
        }
    }
}
