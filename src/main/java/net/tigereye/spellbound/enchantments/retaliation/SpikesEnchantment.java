package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.Objects;

public class SpikesEnchantment extends SBEnchantment {

    public SpikesEnchantment() {
        super(definition(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE,
            Spellbound.config.spikes.WEIGHT, //enchantment weight
            Spellbound.config.spikes.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.spikes.BASE_POWER,Spellbound.config.spikes.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.spikes.BASE_POWER+Spellbound.config.spikes.POWER_RANGE,Spellbound.config.spikes.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.spikes.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.spikes.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.spikes.SOFT_CAP;}
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
