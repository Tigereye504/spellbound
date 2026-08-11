package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class PestilenceEnchantment  extends SBEnchantment {

    public PestilenceEnchantment() {
        super(definition(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE,
            Spellbound.config.pestilence.WEIGHT, //enchantment weight
            Spellbound.config.pestilence.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.pestilence.BASE_POWER,Spellbound.config.pestilence.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.pestilence.BASE_POWER+Spellbound.config.pestilence.POWER_RANGE,Spellbound.config.pestilence.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.pestilence.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.pestilence.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.pestilence.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.pestilence.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.pestilence.IS_FOR_SALE;}
    @Override
    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(defender.getItemBySlot(LivingEntity.getEquipmentSlotForItem(stack)) != stack){
            return amount;
        }
        if(source.getEntity() == null){
            return amount;
        }
        //generate a list of harmful effects
        Collection<MobEffectInstance> effects = defender.getActiveEffects();
        List<MobEffectInstance> hostileEffects = new ArrayList<>();
        effects.forEach(effect -> {
            if(effect.getEffect().value().getCategory() == MobEffectCategory.HARMFUL && !effect.getEffect().value().isInstantenous()){
                hostileEffects.add(effect);
            }
        });
        //create a stink cloud containing those effects and Pestilence
                AreaEffectCloud stank = new AreaEffectCloud(defender.level(),defender.getX(),defender.getY(),defender.getZ());
                AtomicInteger longestDuration = new AtomicInteger(1);
                int levels = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.PESTILENCE,defender);
                stank.setOwner(defender);
                stank.setDuration(Spellbound.config.pestilence.DURATION_PER_LEVEL* levels);
                stank.setRadiusOnUse(0);
                stank.setRadius((float)Math.sqrt(1+(levels * Spellbound.config.pestilence.RADIUS_SQUARED_PER_LEVEL)));
                hostileEffects.forEach(effect -> {
                    int duration = (int) Math.min(effect.getDuration() * Spellbound.config.pestilence.STATUS_DURATION_FACTOR, Spellbound.config.pestilence.MAX_STATUS_DURATION);
                    stank.addEffect(new MobEffectInstance(effect.getEffect(),duration,effect.getAmplifier()));
                    if(duration > longestDuration.get()){
                        longestDuration.set(duration);
                    }
                });
                MobEffectInstance osei = new MobEffectInstance(SBStatusEffects.PESTILENCE,
                        Math.max(Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY,(longestDuration.get() - (longestDuration.get() % Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY))
                                + Spellbound.config.pestilence.PESTILENCE_DAMAGE_FREQUENCY_OFFSET));
                stank.addEffect(osei);
                defender.level().addFreshEntity(stank);
            //}
        //}
        return amount;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() == Items.BOOK
                || super.canEnchant(stack);
    }
}
