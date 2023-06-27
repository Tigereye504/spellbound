package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SpellboundUtil;

public class HoverEnchantment extends SBEnchantment {

    public HoverEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.hover.RARITY), EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.hover.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.hover.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.hover.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.hover.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.hover.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.hover.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.hover.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.hover.IS_FOR_SALE;}
    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if the user has landed since phasing, reset
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.hasMidairJumped() && (entity.isOnGround() || entity.isClimbing() || entity.isSwimming() || entity.isTouchingWater())) {
                player.setHasMidairJumped(false);
            }
        }
    }

    @Override
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){

        if(entity.isSwimming()
        || entity.isTouchingWater()
        || stack != entity.getEquippedStack(EquipmentSlot.LEGS)){
            return;
        }
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.hasMidairJumped()) {
                entity.removeStatusEffect(SBStatusEffects.HOVERING);
                return;
            }
            player.setHasMidairJumped(true);
            NetworkingUtil.sendStatusEffectRequestPacket(
                    Spellbound.config.hover.DURATION_BASE + Spellbound.config.hover.DURATION_PER_LEVEL * level, 0,
                    SBStatusEffects.HOVERING);
        }
    }
}
