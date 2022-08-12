package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;
import org.spongepowered.include.com.google.common.base.Predicates;

import java.util.List;
import java.util.Objects;

public class SpikesEnchantment extends SBEnchantment {

    public SpikesEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.SPIKES_RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.SPIKES_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.SPIKES_POWER_PER_RANK * level) - Spellbound.config.SPIKES_BASE_POWER;
        if(level > Spellbound.config.SPIKES_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.SPIKES_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.SPIKES_HARD_CAP;
        else return 0;
    }

    @Override
    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        List<LivingEntity> entities = entity.getWorld().getEntitiesByClass(LivingEntity.class,
                entity.getBoundingBox().expand(.5,.5,.5),Objects::nonNull);
        if(!entities.isEmpty()) {
            float damage = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.SPIKES, entity)
                    * Spellbound.config.SPIKES_DAMAGE_PER_LEVEL;
            for (LivingEntity target :
                    entities) {
                if (target != entity) {
                    target.damage(DamageSource.thorns(entity), damage);
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
