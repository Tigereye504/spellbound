package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.UUID;

public class GoldskinEnchantment extends SBEnchantment{
    private static final int PRIORITY = -1;
    private static final UUID GOLDSKIN_ID = UUID.fromString("ed0034c7-a983-40b3-ace0-f361d332c9a7");

    public GoldskinEnchantment() {
        super(definition(Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS ? SBTags.ARMOR_AND_SHIELD_ENCHANTABLE : ItemTags.ARMOR_ENCHANTABLE,
            Spellbound.config.goldskin.WEIGHT, //enchantment weight
            Spellbound.config.goldskin.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.goldskin.BASE_POWER,Spellbound.config.goldskin.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.goldskin.BASE_POWER+Spellbound.config.goldskin.POWER_RANGE,Spellbound.config.goldskin.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.goldskin.ANVIL_COST, //level cost at anvil
            Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.goldskin.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.goldskin.SOFT_CAP;}
    @Override
    public int getPriority(){return PRIORITY;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.goldskin.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.goldskin.IS_FOR_SALE;}

    @Override
    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        if(oldLevel > 0 || newLevel > 0) {
            resetGoldskin(entity);
        }
        AttributeInstance att = entity.getAttribute(Attributes.MAX_ABSORPTION);
        if(att != null) {
            AttributeModifier mod = new AttributeModifier(GOLDSKIN_ID, "SpellboundGoldskinMaxAbsorption",
                    this.calculateMaxAbsorption(entity)
                    ,AttributeModifier.Operation.ADD_VALUE);
            SpellboundUtil.ReplaceAttributeModifier(att, mod);
        }
    }

    @Override
    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        resetGoldskin(defender);
        return amount;
    }

    @Override
    public void onTakeRedHealthDamageOnce(int level, ItemStack itemStack, DamageSource source, LivingEntity entity, float amount) {
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
        Iterable<ItemStack> gear = entity.getAllSlots();
        int totalLevels = 0;
        int instanceCount = 0;
        for(ItemStack item : gear){
            if(SBEnchantmentHelper.isEquipmentCorrectlyWorn(item,entity)){
                int itemLevel = EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.GOLDSKIN, item);
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
