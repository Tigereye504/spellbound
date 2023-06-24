package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SkotosyntheticEnchantment extends SBEnchantment {

    public SkotosyntheticEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.SKOTOSYNTHETIC_RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.SKOTOSYNTHETIC_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.SKOTOSYNTHETIC_POWER_PER_RANK * level) + Spellbound.config.SKOTOSYNTHETIC_BASE_POWER;
        if(level > Spellbound.config.SKOTOSYNTHETIC_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.SKOTOSYNTHETIC_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.SKOTOSYNTHETIC_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onInventoryTick(int level, ItemStack stack, World world, Entity entity, int slot, boolean selected){
        if(!world.isClient() && stack.isDamaged()){
            if(selected && entity instanceof PlayerEntity && ((PlayerEntity) entity).handSwinging){
                return;
            }
            int light = world.getLightLevel(entity.getBlockPos());
            if(light > Spellbound.config.SKOTOSYNTHETIC_LIGHT_MAXIMUM){
                return;
            }
            int periodMultiplier = Math.max(1,light+1);
            if(entity.world.getTime() % ((long) Spellbound.config.SKOTOSYNTHETIC_REPAIR_PERIOD*periodMultiplier) == 0){
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return false;
    }
}
