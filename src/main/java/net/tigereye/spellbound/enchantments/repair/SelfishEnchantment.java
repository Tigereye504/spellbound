package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SelfishEnchantment extends SBEnchantment {

    public SelfishEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.selfish.RARITY), EnchantmentCategory.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
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
    public boolean isTreasureOnly() {return Spellbound.config.selfish.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.selfish.IS_FOR_SALE;}

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(!entity.level().isClientSide() && stack.isDamaged()){
            ItemStack target;
            int targetSlot = (int) (entity.level().getGameTime() % 7);
            switch (targetSlot) {
                case 0 -> target = entity.getItemBySlot(EquipmentSlot.MAINHAND);
                case 1 -> target = entity.getItemBySlot(EquipmentSlot.OFFHAND);
                case 2 -> target = entity.getItemBySlot(EquipmentSlot.HEAD);
                case 3 -> target = entity.getItemBySlot(EquipmentSlot.CHEST);
                case 4 -> target = entity.getItemBySlot(EquipmentSlot.LEGS);
                case 5 -> target = entity.getItemBySlot(EquipmentSlot.FEET);
                default -> {
                    return;
                }
            }
            if(target.isDamageableItem()
                        && target.getDamageValue() < target.getMaxDamage() - 1
                        && !EnchantmentHelper.getEnchantments(target).containsKey(SBEnchantments.SELFISH)){
                ServerPlayer player = null;
                if(entity instanceof ServerPlayer){player = (ServerPlayer)entity;}
                target.hurt(1,entity.getRandom(),player);
                stack.setDamageValue(stack.getDamageValue()-1);
            }
        }
    }

}
