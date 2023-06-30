package net.tigereye.spellbound.enchantments.meta;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class MonogamousEnchantment extends SBEnchantment {

    public MonogamousEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.monogamous.RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.monogamous.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.monogamous.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.monogamous.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.monogamous.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.monogamous.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.monogamous.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.monogamous.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.monogamous.IS_FOR_SALE;}
    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public float getArmorAmount(int level, ItemStack stack, LivingEntity entity) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,entity);
        if(entity.hasStatusEffect(SBStatusEffects.MONOGAMY)){
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

}
