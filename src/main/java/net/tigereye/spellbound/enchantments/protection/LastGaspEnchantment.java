package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.DyingEffect;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class LastGaspEnchantment extends SBEnchantment{

    public LastGaspEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.lastGasp.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.lastGasp.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.lastGasp.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.lastGasp.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.lastGasp.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.lastGasp.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.lastGasp.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.lastGasp.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.lastGasp.IS_FOR_SALE;}

    @Override
    public boolean onLethalDamageOnce(int level, DamageSource source, LivingEntity entity){
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            EntityAttributeModifier mod = att.getModifier(DyingEffect.DYING_HEATLH_ID);
            if (mod != null && mod.getValue() <= -1) {
                return false;
            }
        }
        int levels = SBEnchantmentHelper.getSpellboundEnchantmentAmountCorrectlyWorn(SBEnchantments.LAST_GASP,entity);
        entity.setHealth(entity.getMaxHealth()*levels*Spellbound.config.lastGasp.HEALTH_REBOUND_PER_RANK);
        int severity = 0;
        if(entity.hasStatusEffect(SBStatusEffects.DYING)){
            severity = entity.getStatusEffect(SBStatusEffects.DYING).getAmplifier() + 1;
        }
        entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.DYING, Spellbound.config.lastGasp.TIME_TO_DIE+1
                , severity,false,true,true));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 72000
                , severity,false,false,false));
        entity.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 72000
                , severity,false,false,false));

        //draw particals between entity and anchor

        int particles = (severity+1)*5;
        for (int i = 0; i < particles; i++) {
            float driftX = (entity.getRandom().nextFloat() - .5f) * .15f;
            float driftY = (entity.getRandom().nextFloat() - .5f) * .15f;
            float driftZ = (entity.getRandom().nextFloat() - .5f) * .15f;
            entity.world.addParticle(ParticleTypes.FALLING_LAVA,
                    entity.getX() + driftX, entity.getY() + driftY, entity.getZ() + driftZ,
                    driftX, driftY, driftZ);
        }
        return true;
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        if(killer.hasStatusEffect(SBStatusEffects.DYING)){
            killer.clearStatusEffects();
        }
    }

    @Override
    public void onGainExperienceAlways(PlayerEntity player, int amount){
        EntityAttributeInstance att = player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            EntityAttributeModifier mod = att.getModifier(DyingEffect.DYING_HEATLH_ID);
            double value = 0;
            if(mod != null){
                value = mod.getValue() + (amount * Spellbound.config.lastGasp.RECOVERY_FROM_EXPERIENCE);
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
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            att.removeModifier(DyingEffect.DYING_HEATLH_ID);
        }
    }
}
