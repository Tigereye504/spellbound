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

public class MonogamousEnchantment extends SBEnchantment {

    public MonogamousEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.MONOGAMOUS_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public int getMinPower(int level) {
        return 5;
    }

    @Override
    public int getMaxPower(int level) {
        return 51;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.MONOGAMOUS_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 1;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public float getProtectionAmount(int level, DamageSource source, ItemStack stack, LivingEntity target) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,target);
        if(target.hasStatusEffect(SBStatusEffects.MONOGAMY)){
            return 2;
        }
        return -4;
    }

    @Override
    public float getAttackDamage(int level, ItemStack stack, net.minecraft.entity.LivingEntity attacker, Entity defender) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,attacker);
        if(attacker.hasStatusEffect(SBStatusEffects.MONOGAMY)){
            return 4;
        }
        return -6;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            SBEnchantmentHelper.testOwnerFaithfulness(stack,(LivingEntity)attacker);
            if(((LivingEntity)attacker).hasStatusEffect(SBStatusEffects.MONOGAMY)){
                return damage * 1.2f;
            }
            return damage *.7f;
        }
        return damage;
    }

    @Override
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        SBEnchantmentHelper.testOwnerFaithfulness(itemStack,playerEntity);
        if(playerEntity.hasStatusEffect(SBStatusEffects.MONOGAMY)){
            return miningSpeed*1.4f;
        }
        return miningSpeed*.5f;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

}
