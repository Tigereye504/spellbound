package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.Objects;

public class SpikesEnchantment extends SBEnchantment {

    public SpikesEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.spikes.RARITY), EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND},true);
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
    public boolean isTreasureOnly() {return Spellbound.config.spikes.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.spikes.IS_FOR_SALE;}
    @Override
    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity user){
        List<LivingEntity> entities = user.level().getEntitiesOfClass(LivingEntity.class,
                user.getBoundingBox().inflate(.5,.5,.5),Objects::nonNull);
        if(!entities.isEmpty()) {
            float damage = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.SPIKES, user)
                    * Spellbound.config.spikes.DAMAGE_PER_LEVEL;
            for (LivingEntity target :
                    entities) {
                if (target != user
                        && !(user.hasIndirectPassenger(target) || target.hasIndirectPassenger(user)))
                {
                    target.hurt(user.damageSources().thorns(user), damage);
                }
            }
        }
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() == Items.BOOK
                || super.canEnchant(stack);
    }

}
