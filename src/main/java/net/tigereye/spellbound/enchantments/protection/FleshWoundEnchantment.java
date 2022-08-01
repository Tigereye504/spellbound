package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.*;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class FleshWoundEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public FleshWoundEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.FLESH_WOUND_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public int getMinPower(int level) {
        return (level*11)-10;
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.FLESH_WOUND_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 4;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

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
        entity.setAbsorptionAmount(absorption+(level*amount*Spellbound.config.FLESH_WOUND_ABSORPTION_PER_DAMAGE_PER_LEVEL));
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }

    private static void ReplaceAttributeModifier(EntityAttributeInstance att, EntityAttributeModifier mod)
    {
        //removes any existing mod and replaces it with the updated one.
        att.removeModifier(mod);
        att.addPersistentModifier(mod);
    }
}
