package net.tigereye.spellbound.enchantments.meta;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PolygamousEnchantment extends SBEnchantment {

    public PolygamousEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.polygamous.RARITY), EnchantmentCategory.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.polygamous.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.polygamous.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.polygamous.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.polygamous.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.polygamous.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.polygamous.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.polygamous.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.polygamous.IS_FOR_SALE;}
    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public float getArmorAmount(int level, ItemStack stack, LivingEntity entity) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,entity);
        if(entity.hasEffect(SBStatusEffects.POLYGAMY)){
            return .5f;
        }
        return -1;
    }

    @Override
    public float getDamageBonus(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        SBEnchantmentHelper.testOwnerFaithfulness(stack,attacker);
        if(attacker.hasEffect(SBStatusEffects.POLYGAMY)){
            return 2;
        }
        return -4;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, AbstractArrow projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            SBEnchantmentHelper.testOwnerFaithfulness(stack, (LivingEntity)attacker);
            if (((LivingEntity)attacker).hasEffect(SBStatusEffects.POLYGAMY)) {
                return damage*1.1f;
            }
            return damage*.8f;
        }
        return damage;
    }

    @Override
    public float getMiningSpeed(int level, Player playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        SBEnchantmentHelper.testOwnerFaithfulness(itemStack,playerEntity);
        if(playerEntity.hasEffect(SBStatusEffects.POLYGAMY)){
            return miningSpeed*1.2f;
        }
        return miningSpeed*.7f;
    }
}
