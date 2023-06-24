package net.tigereye.spellbound.enchantments.personality;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PolygamousEnchantment extends SBEnchantment {

    public PolygamousEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.POLYGAMOUS_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.POLYGAMOUS_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.POLYGAMOUS_POWER_PER_RANK * level) + Spellbound.config.POLYGAMOUS_BASE_POWER;
        if(level > Spellbound.config.POLYGAMOUS_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.POLYGAMOUS_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.POLYGAMOUS_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public float getArmorAmount(int level, ItemStack stack, LivingEntity entity) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,entity);
        if(entity.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            return .5f;
        }
        return -1;
    }

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,attacker);
        if(attacker.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            return 2;
        }
        return -4;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            SBEnchantmentHelper.testOwnerFaithfulness(stack, (LivingEntity)attacker);
            if (((LivingEntity)attacker).hasStatusEffect(SBStatusEffects.POLYGAMY)) {
                return damage*1.1f;
            }
            return damage*.8f;
        }
        return damage;
    }

    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        SBEnchantmentHelper.testOwnerFaithfulness(itemStack,playerEntity);
        if(playerEntity.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            return miningSpeed*1.2f;
        }
        return miningSpeed*.7f;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

}
