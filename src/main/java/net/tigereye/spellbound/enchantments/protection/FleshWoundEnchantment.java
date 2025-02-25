package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;

public class FleshWoundEnchantment extends SBEnchantment{

    public FleshWoundEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.fleshWound.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.fleshWound.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.fleshWound.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.fleshWound.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.fleshWound.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.fleshWound.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.fleshWound.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.fleshWound.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.fleshWound.IS_FOR_SALE;}

    @Override
    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        if(oldLevel > 0 || newLevel > 0) {
            resetFleshWound(entity);
        }
    }

    @Override
    public void onRedHealthDamageOnce(int level, ItemStack itemStack, DamageSource source, LivingEntity entity, float amount) {
        resetFleshWound(entity);
    }

    private void resetFleshWound(LivingEntity entity){
        if(entity instanceof SpellboundLivingEntity slEntity) {
            List<DelayedAction> actions = slEntity.spellbound$getDelayedActions();
            DelayedAction actionToRemove = null;
            for(DelayedAction action : actions){
                if(action instanceof FleshWoundStartHealingAction || action instanceof FleshWoundHealAction){
                    actionToRemove = action;
                }
            }
            if(actionToRemove != null){
                actions.remove(actionToRemove);
            }
            slEntity.spellbound$addDelayedAction(new FleshWoundEnchantment.FleshWoundStartHealingAction(entity, calculateMaxAbsorption(entity)));
        }
    }

    private float calculateMaxAbsorption(LivingEntity entity){
        Iterable<ItemStack> gear = entity.getItemsEquipped();
        int totalLevels = 0;
        int instanceCount = 0;
        for(ItemStack item : gear){
            if(SBEnchantmentHelper.isEquipmentCorrectlyWorn(item,entity)){
                int itemLevel = EnchantmentHelper.getLevel(SBEnchantments.FLESH_WOUND, item);
                if(itemLevel > 0) {
                    if (item.getItem() == Items.LEATHER_BOOTS ||
                            item.getItem() == Items.LEATHER_CHESTPLATE ||
                            item.getItem() == Items.LEATHER_LEGGINGS ||
                            item.getItem() == Items.LEATHER_HELMET) {
                        itemLevel *= 3;
                    }
                    totalLevels += itemLevel;
                    instanceCount++;
                }
            }
        }
        return entity.getMaxHealth() * ((instanceCount * Spellbound.config.fleshWound.BASE_ABSORPTION) + (totalLevels * Spellbound.config.fleshWound.ABSORPTION_PER_LEVEL));
    }

    private static class FleshWoundStartHealingAction extends DelayedAction {

        LivingEntity owner;
        float finalAbsorption;

        FleshWoundStartHealingAction(LivingEntity owner, float finalAbsorption){
            this.owner = owner;
            this.finalAbsorption = finalAbsorption;
            this.setTicks(Spellbound.config.fleshWound.TIME_UNTIL_RESET);
        }
        @Override
        public void act() {
            if(!owner.isAlive()){
                return;
            }
            if(owner instanceof SpellboundLivingEntity sleOwner){
                sleOwner.spellbound$addDelayedAction(new FleshWoundHealAction(owner,finalAbsorption));
            }
        }
    }

    private static class FleshWoundHealAction extends DelayedAction {

        LivingEntity owner;
        float finalAbsorption;

        FleshWoundHealAction(LivingEntity owner, float finalAbsorption){
            this.owner = owner;
            this.finalAbsorption = finalAbsorption;
        }

        @Override
        public void act() {
            if(!owner.isAlive()){
                return;
            }
            if(owner.getAbsorptionAmount() > finalAbsorption){
                return;
            }
            float newAbsorption = owner.getAbsorptionAmount() + Spellbound.config.fleshWound.ABSORPTION_PER_TICK;
            if(newAbsorption > finalAbsorption){
                owner.setAbsorptionAmount(finalAbsorption);
            }
            else{
                owner.setAbsorptionAmount(newAbsorption);
                if(owner instanceof SpellboundLivingEntity sleOwner){
                    sleOwner.spellbound$addDelayedAction(this);
                }
            }
        }
    }
}
