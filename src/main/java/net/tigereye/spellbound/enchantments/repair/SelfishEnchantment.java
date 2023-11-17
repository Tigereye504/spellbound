package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SelfishEnchantment extends SBEnchantment {

    public SelfishEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.selfish.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.selfish.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.selfish.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.selfish.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.selfish.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.selfish.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.selfish.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.selfish.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.selfish.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(!entity.getWorld().isClient() && stack.isDamaged()){
            ItemStack target;
            int targetSlot = (int) (entity.getWorld().getTime() % 7);
            switch (targetSlot) {
                case 0 -> target = entity.getEquippedStack(EquipmentSlot.MAINHAND);
                case 1 -> target = entity.getEquippedStack(EquipmentSlot.OFFHAND);
                case 2 -> target = entity.getEquippedStack(EquipmentSlot.HEAD);
                case 3 -> target = entity.getEquippedStack(EquipmentSlot.CHEST);
                case 4 -> target = entity.getEquippedStack(EquipmentSlot.LEGS);
                case 5 -> target = entity.getEquippedStack(EquipmentSlot.FEET);
                default -> {
                    return;
                }
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

}
