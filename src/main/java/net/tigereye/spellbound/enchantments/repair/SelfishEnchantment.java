package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.Iterator;

public class SelfishEnchantment extends SBEnchantment {

    public SelfishEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.SELFISH_RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.SELFISH_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.SELFISH_POWER_PER_RANK * level) + Spellbound.config.SELFISH_BASE_POWER;
        if(level > Spellbound.config.SELFISH_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.SELFISH_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.SELFISH_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(!entity.world.isClient() && stack.isDamaged()){
            Iterator<ItemStack> i = entity.getItemsEquipped().iterator();
            ItemStack target;
            int targetSlot = (int) (entity.world.getTime() % 7);
            switch(targetSlot){
                case 0:
                    target = entity.getEquippedStack(EquipmentSlot.MAINHAND);
                    break;
                case 1:
                    target = entity.getEquippedStack(EquipmentSlot.OFFHAND);
                    break;
                case 2:
                    target = entity.getEquippedStack(EquipmentSlot.HEAD);
                    break;
                case 3:
                    target = entity.getEquippedStack(EquipmentSlot.CHEST);
                    break;
                case 4:
                    target = entity.getEquippedStack(EquipmentSlot.LEGS);
                    break;
                case 5:
                    target = entity.getEquippedStack(EquipmentSlot.FEET);
                    break;
                default:
                    return;
            }
            if(target.isDamageable()
                        && target.getDamage() < target.getMaxDamage() - 1
                        && !EnchantmentHelper.get(target).containsKey(SBEnchantments.SELFISH)){
                ServerPlayerEntity player = null;
                if(entity instanceof ServerPlayerEntity){player = (ServerPlayerEntity)entity;}
                target.damage(1,entity.getRandom(),player);
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

}
