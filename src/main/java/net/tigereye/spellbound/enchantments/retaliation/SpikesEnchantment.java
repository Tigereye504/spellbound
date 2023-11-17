package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageSources;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.Objects;

public class SpikesEnchantment extends SBEnchantment {

    public SpikesEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.spikes.RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.spikes.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.spikes.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.spikes.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.spikes.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.spikes.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.spikes.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.spikes.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.spikes.IS_FOR_SALE;}
    @Override
    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        List<LivingEntity> entities = entity.getWorld().getEntitiesByClass(LivingEntity.class,
                entity.getBoundingBox().expand(.5,.5,.5),Objects::nonNull);
        if(!entities.isEmpty()) {
            float damage = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.SPIKES, entity)
                    * Spellbound.config.spikes.DAMAGE_PER_LEVEL;
            for (LivingEntity target :
                    entities) {
                if (target != entity) {
                    target.damage(entity.getDamageSources().thorns(entity), damage);
                }
            }
        }
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() == Items.BOOK
                || super.isAcceptableItem(stack);
    }

}
