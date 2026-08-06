package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SelfishEnchantment extends SBEnchantment {

    public SelfishEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.selfish.RARITY), //enchantment weight
            Spellbound.config.selfish.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.selfish.BASE_POWER,Spellbound.config.selfish.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.selfish.BASE_POWER+Spellbound.config.selfish.POWER_RANGE,Spellbound.config.selfish.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.selfish.RARITY-1), //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.MAINHAND}),
            false); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.selfish.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.selfish.SOFT_CAP;}
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
            EquipmentSlot targetSlot;
            switch ((int) entity.level().getGameTime() % 7) {
                case 0 -> targetSlot = EquipmentSlot.MAINHAND;
                case 1 -> targetSlot = EquipmentSlot.OFFHAND;
                case 2 -> targetSlot = EquipmentSlot.HEAD;
                case 3 -> targetSlot = EquipmentSlot.CHEST;
                case 4 -> targetSlot = EquipmentSlot.LEGS;
                case 5 -> targetSlot = EquipmentSlot.FEET;
                default -> {
                    return;
                }
            }
            ItemStack target = entity.getItemBySlot(targetSlot);
            if(target.isDamageableItem()
                        && target.getDamageValue() < target.getMaxDamage() - 1
                        && target.getEnchantments().getLevel(SBEnchantments.SELFISH) == 0){
                ServerPlayer player = null;
                if(entity instanceof ServerPlayer){player = (ServerPlayer)entity;}
                target.hurtAndBreak(1,player,targetSlot);
                stack.setDamageValue(stack.getDamageValue()-1);
            }
        }
    }

}
