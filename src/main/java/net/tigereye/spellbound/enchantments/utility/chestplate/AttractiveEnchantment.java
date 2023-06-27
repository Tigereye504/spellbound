package net.tigereye.spellbound.enchantments.utility.chestplate;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class AttractiveEnchantment extends SBEnchantment{

    public AttractiveEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.attractive.RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.attractive.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.attractive.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.attractive.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.attractive.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.attractive.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.attractive.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.attractive.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.attractive.IS_FOR_SALE;}

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.getEntityWorld();
        if(!world.isClient()){
            if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
                return;
            }
            SpellboundUtil.pushPullEntitiesPlayersInRange(Spellbound.config.attractive.RANGE, Spellbound.config.attractive.STRENGTH, entity);
        }
    }
}
