package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SpellboundUtil;

public class HoverEnchantment extends SBEnchantment {

    public HoverEnchantment() {
        super(definition(ItemTags.LEG_ARMOR_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.hover.RARITY), //enchantment weight
            Spellbound.config.hover.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.hover.BASE_POWER,Spellbound.config.hover.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.hover.BASE_POWER+Spellbound.config.hover.POWER_RANGE,Spellbound.config.hover.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.hover.RARITY-1), //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.LEGS}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.hover.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.hover.SOFT_CAP;}
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
                    SBStatusEffects.HOVERING.value());
        }
    }
}
