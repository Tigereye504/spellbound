package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class FleshWoundEnchantment extends SBEnchantment{

    public FleshWoundEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.fleshWound.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.fleshWound.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.fleshWound.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.fleshWound.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.fleshWound.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.fleshWound.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.fleshWound.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.fleshWound.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.fleshWound.IS_FOR_SALE;}

    @Override
    public void onRedHealthDamage(int level, ItemStack itemStack, LivingEntity entity, float amount) {
        if(entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(itemStack)) != itemStack){
            return;
        }
        if(itemStack.getItem() == Items.LEATHER_BOOTS ||
                itemStack.getItem() == Items.LEATHER_CHESTPLATE ||
                itemStack.getItem() == Items.LEATHER_LEGGINGS ||
                itemStack.getItem() == Items.LEATHER_HELMET){
            //if(((DyeableArmorItem) itemStack.getItem()).getColor(itemStack) == 1908001) {
            //    level *= 3;
            //}
            level *= 3;
        }
        float absorption = entity.getAbsorptionAmount();
        entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.BRAVADOS, 1200, 0,false,false,false));
        entity.setAbsorptionAmount(absorption+(level*amount*Spellbound.config.fleshWound.ABSORPTION_PER_DAMAGE_PER_LEVEL));
    }
}
