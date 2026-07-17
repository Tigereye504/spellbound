package net.tigereye.spellbound.enchantments.repair;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class SkotosyntheticEnchantment extends SBEnchantment {

    public SkotosyntheticEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.skotosynthetic.RARITY), EnchantmentCategory.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
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
    public boolean isTreasureOnly() {return Spellbound.config.skotosynthetic.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.skotosynthetic.IS_FOR_SALE;}
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
            if(light > Spellbound.config.skotosynthetic.LIGHT_MAXIMUM){
                return;
            }
            int periodMultiplier = Math.max(1,light+1);
            if(entity.level().getGameTime() % ((long) Spellbound.config.skotosynthetic.REPAIR_PERIOD*periodMultiplier) == 0){
                stack.setDamageValue(stack.getDamageValue()-1);
            }
        }
    }
}
