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

public class PhotosyntheticEnchantment extends SBEnchantment {

    public PhotosyntheticEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.photosynthetic.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.photosynthetic.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.photosynthetic.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.photosynthetic.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.photosynthetic.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.photosynthetic.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.photosynthetic.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.photosynthetic.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.photosynthetic.IS_FOR_SALE;}

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
            if(light < Spellbound.config.photosynthetic.LIGHT_MINIMUM){
                return;
            }
            int periodMultiplier = Math.max(1,16 - light);
            if(entity.getWorld().getTime() % ((long) Spellbound.config.photosynthetic.REPAIR_PERIOD*periodMultiplier) == 0){
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }
}
