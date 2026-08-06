package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class PhotosyntheticEnchantment extends SBEnchantment {

    public PhotosyntheticEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.photosynthetic.RARITY), //enchantment weight
            Spellbound.config.photosynthetic.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.photosynthetic.BASE_POWER,Spellbound.config.photosynthetic.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.photosynthetic.BASE_POWER+Spellbound.config.photosynthetic.POWER_RANGE,Spellbound.config.photosynthetic.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.photosynthetic.RARITY-1), //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.MAINHAND}),
            false); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.photosynthetic.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.photosynthetic.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.photosynthetic.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.photosynthetic.IS_FOR_SALE;}

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public void onInventoryTick(int level, ItemStack stack, Level world, Entity entity, int slot, boolean selected){
        if(!world.isClientSide() && stack.isDamaged()){
            if(selected && entity instanceof Player && ((Player) entity).swinging){
                return;
            }
            int light = world.getMaxLocalRawBrightness(entity.blockPosition());
            if(light < Spellbound.config.photosynthetic.LIGHT_MINIMUM){
                return;
            }
            int periodMultiplier = Math.max(1,16 - light);
            if(entity.level().getGameTime() % ((long) Spellbound.config.photosynthetic.REPAIR_PERIOD*periodMultiplier) == 0){
                stack.setDamageValue(stack.getDamageValue()-1);
            }
        }
    }
}
