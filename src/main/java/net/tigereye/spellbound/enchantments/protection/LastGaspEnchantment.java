package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.DyingEffect;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class LastGaspEnchantment extends SBEnchantment{

    public LastGaspEnchantment() {
        super(definition(Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS ? SBTags.ARMOR_AND_SHIELD_ENCHANTABLE : ItemTags.ARMOR_ENCHANTABLE,
            Spellbound.config.lastGasp.WEIGHT, //enchantment weight
            Spellbound.config.lastGasp.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.lastGasp.BASE_POWER,Spellbound.config.lastGasp.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.lastGasp.BASE_POWER+Spellbound.config.lastGasp.POWER_RANGE,Spellbound.config.lastGasp.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.lastGasp.ANVIL_COST, //level cost at anvil
            Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.lastGasp.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.lastGasp.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.lastGasp.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.lastGasp.IS_FOR_SALE;}

    @Override
    public boolean onLethalDamageOnce(int level, DamageSource source, LivingEntity entity){
        AttributeInstance att = entity.getAttribute(Attributes.MAX_HEALTH);
        double currentHealthLost = 0;
        if(att != null) {
            AttributeModifier mod = att.getModifier(DyingEffect.DYING_HEATLH_ID);
            if(mod != null) {
                currentHealthLost = mod.amount();
                if (currentHealthLost <= -.99) {
                    return false;
                }
            }
        }
        int levels = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.LAST_GASP,entity);
        double lossFactor = Spellbound.config.lastGasp.INSTANT_MAX_HEALTH_LOSS_FACTOR * (1 - levels/16.0);
        DyingEffect.UpdateDyingModifier(entity,currentHealthLost - ((1+currentHealthLost)*lossFactor));
        entity.setHealth(entity.getMaxHealth()*levels*Spellbound.config.lastGasp.HEALTH_REBOUND_PER_RANK);

        int severity = 0;
        if(entity.hasEffect(SBStatusEffects.DYING)){
            severity = entity.getEffect(SBStatusEffects.DYING).getAmplifier() + 1;
        }
        int duration = Spellbound.config.lastGasp.SECONDS_TO_DIE*20;
        entity.addEffect(new MobEffectInstance(SBStatusEffects.DYING, duration
                , severity,false,true,true));
        entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration
                , severity,false,false,false));
        entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration
                , severity,false,false,false));
        if(Spellbound.config.lastGasp.TEXT_PROMPT && entity instanceof Player pEntity) {
            pEntity.displayClientMessage(Component.translatable("enchantment.spellbound.last_gasp.message.dying"), true);
        }
        //draw particles between entity and anchor

        int particles = (severity+1)*5;
        for (int i = 0; i < particles; i++) {
            float driftX = (entity.getRandom().nextFloat() - .5f) * .15f;
            float driftY = (entity.getRandom().nextFloat() - .5f) * .15f;
            float driftZ = (entity.getRandom().nextFloat() - .5f) * .15f;
            entity.level().addParticle(ParticleTypes.FALLING_LAVA,
                    entity.getX() + driftX, entity.getY() + driftY, entity.getZ() + driftZ,
                    driftX, driftY, driftZ);
        }
        return true;
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        if(killer.hasEffect(SBStatusEffects.DYING)){
            killer.removeAllEffects();
        }
    }

    @Override
    public void onGainExperienceAlways(Player player, int amount){
        AttributeInstance att = player.getAttribute(Attributes.MAX_HEALTH);
        if(att != null) {
            AttributeModifier mod = att.getModifier(DyingEffect.DYING_HEATLH_ID);
            double value;
            if(mod != null){
                value = mod.amount() + (amount * Spellbound.config.lastGasp.RECOVERY_FROM_EXPERIENCE);
                if(value < 0){
                    DyingEffect.UpdateDyingModifier(player,value);
                }
                else {
                    att.removeModifier(DyingEffect.DYING_HEATLH_ID);
                }
            }
        }
    }

    public void onStartSleepingAlways(LivingEntity entity){
        AttributeInstance att = entity.getAttribute(Attributes.MAX_HEALTH);
        if(att != null) {
            att.removeModifier(DyingEffect.DYING_HEATLH_ID);
        }
    }
}
