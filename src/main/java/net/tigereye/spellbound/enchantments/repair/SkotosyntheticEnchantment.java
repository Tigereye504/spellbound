package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SkotosyntheticEnchantment extends SBEnchantment {

    public SkotosyntheticEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.skotosynthetic.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.skotosynthetic.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.skotosynthetic.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.skotosynthetic.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.skotosynthetic.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.skotosynthetic.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.skotosynthetic.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.skotosynthetic.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.skotosynthetic.IS_FOR_SALE;}
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
            if(light > Spellbound.config.skotosynthetic.LIGHT_MAXIMUM){
                return;
            }
            int periodMultiplier = Math.max(1,light+1);
            if(entity.getWorld().getTime() % ((long) Spellbound.config.skotosynthetic.REPAIR_PERIOD*periodMultiplier) == 0){
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }
}
