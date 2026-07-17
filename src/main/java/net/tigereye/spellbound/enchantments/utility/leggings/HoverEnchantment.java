package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SpellboundUtil;

public class HoverEnchantment extends SBEnchantment {

    public HoverEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.hover.RARITY), EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS},true);
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
    public boolean isTreasureOnly() {return Spellbound.config.hover.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.hover.IS_FOR_SALE;}
    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if the user has landed since phasing, reset
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.spellbound$hasMidairJumped() && (entity.onGround() || entity.onClimbable() || entity.isSwimming() || entity.isInWater())) {
                player.spellbound$setHasMidairJumped(false);
            }
        }
    }

    @Override
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){

        if(entity.isSwimming()
        || entity.isInWater()
        || stack != entity.getItemBySlot(EquipmentSlot.LEGS)){
            return;
        }
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.spellbound$hasMidairJumped()) {
                entity.removeEffect(SBStatusEffects.HOVERING);
                return;
            }
            player.spellbound$setHasMidairJumped(true);
            NetworkingUtil.sendStatusEffectRequestPacket(
                    Spellbound.config.hover.DURATION_BASE + Spellbound.config.hover.DURATION_PER_LEVEL * level, 0,
                    SBStatusEffects.HOVERING);
        }
    }
}
