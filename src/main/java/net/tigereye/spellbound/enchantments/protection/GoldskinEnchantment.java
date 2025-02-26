package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;

public class GoldskinEnchantment extends SBEnchantment{

    public GoldskinEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.goldskin.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.goldskin.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.goldskin.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.goldskin.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.goldskin.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.goldskin.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.goldskin.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.goldskin.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.goldskin.IS_FOR_SALE;}

    @Override
    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        if(oldLevel > 0 || newLevel > 0) {
            resetGoldskin(entity);
        }
    }

    @Override
    public void onRedHealthDamageOnce(int level, ItemStack itemStack, DamageSource source, LivingEntity entity, float amount) {
        resetGoldskin(entity);
    }

    private void resetGoldskin(LivingEntity entity){
        if(entity instanceof SpellboundLivingEntity slEntity) {
            List<DelayedAction> actions = slEntity.spellbound$getDelayedActions();
            DelayedAction actionToRemove = null;
            for(DelayedAction action : actions){
                if(action instanceof GoldskinStartHealingAction || action instanceof GoldskinHealAction){
                    actionToRemove = action;
                }
            }
            if(actionToRemove != null){
                actions.remove(actionToRemove);
            }
            slEntity.spellbound$addDelayedAction(new GoldskinEnchantment.GoldskinStartHealingAction(entity, calculateMaxAbsorption(entity)));
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
                    totalLevels += itemLevel;
                    instanceCount++;
                }
            }
        }
        return entity.getMaxHealth() * ((instanceCount * Spellbound.config.goldskin.BASE_ABSORPTION) + (totalLevels * Spellbound.config.goldskin.ABSORPTION_PER_LEVEL));
    }

    private static class GoldskinStartHealingAction extends DelayedAction {

        LivingEntity owner;
        float finalAbsorption;

        GoldskinStartHealingAction(LivingEntity owner, float finalAbsorption){
            this.owner = owner;
            this.finalAbsorption = finalAbsorption;
            this.setTicks(Spellbound.config.goldskin.TIME_UNTIL_RESET);
        }
        @Override
        public void act() {
            if(!owner.isAlive()){
                return;
            }
            if(owner instanceof SpellboundLivingEntity sleOwner){
                sleOwner.spellbound$addDelayedAction(new GoldskinHealAction(owner,finalAbsorption));
            }
        }
    }

    private static class GoldskinHealAction extends DelayedAction {

        LivingEntity owner;
        float finalAbsorption;

        GoldskinHealAction(LivingEntity owner, float finalAbsorption){
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
            float newAbsorption = owner.getAbsorptionAmount() + (owner.getMaxHealth() * Spellbound.config.goldskin.ABSORPTION_PER_TICK);
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
