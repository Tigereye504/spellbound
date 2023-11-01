package net.tigereye.spellbound.mob_effect.instance;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.registration.SBStatusEffects;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class OwnedStatusEffectInstance extends StatusEffectInstance{
    public LivingEntity owner = null;
    public UUID ownerUUID = null;

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffect statusEffect) {
        super(statusEffect);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffect statusEffect, int duration) {
        super(statusEffect, duration);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffect statusEffect, int duration, int amplifier) {
        super(statusEffect, duration, amplifier);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean visible) {
        super(statusEffect, duration, amplifier, ambient, visible);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    public OwnedStatusEffectInstance(UUID ownerUUID, StatusEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(SBStatusEffects.PESTILENCE, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.ownerUUID = ownerUUID;
    }

    public OwnedStatusEffectInstance(StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        if(statusEffectInstance instanceof OwnedStatusEffectInstance){
            this.owner = ((OwnedStatusEffectInstance) statusEffectInstance).owner;
            if(owner != null) {
                this.ownerUUID = owner.getUuid();
            }
        }
    }

    public OwnedStatusEffectInstance(@Nullable LivingEntity owner, StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUuid();
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.shouldShowParticles());
        tag.putBoolean("ShowIcon", this.shouldShowIcon());
        if(ownerUUID != null) {
            tag.putUuid("OwnerUUID", ownerUUID);
        }
        return tag;
    }

    public static OwnedStatusEffectInstance customFromNbt(StatusEffect type, NbtCompound tag) {
        int amplifier = tag.getByte("Amplifier");
        int duration = tag.getInt("Duration");
        boolean ambient = tag.getBoolean("Ambient");
        boolean showParticles = true;
        UUID ownerUUID = null;
        if (tag.contains("ShowParticles", 1)) {
            showParticles = tag.getBoolean("ShowParticles");
        }

        boolean showIcon = showParticles;
        if (tag.contains("ShowIcon", 1)) {
            showIcon = tag.getBoolean("ShowIcon");
        }

        if(tag.contains("OwnerUUID")){
            ownerUUID = tag.getUuid("OwnerUUID");
        }
        return new OwnedStatusEffectInstance(ownerUUID,type,duration,amplifier,ambient,showParticles,showIcon,null,Optional.empty());
    }

    /*public boolean fillMissingOwnerData(){
        if(this.owner == null){
            if(this.ownerUUID == null) {
                return false;
            }
            else{
                ServerWorld world;
                if(entity.world instanceof ServerWorld){
                    world = (ServerWorld)entity.world;
                }
                else{
                    return false;
                }
                Entity owner = world.getEntity(pestilenceInstance.ownerUUID);
                if(owner instanceof LivingEntity lEntity) {
                    pestilenceInstance.owner = lEntity;
                }
                if(pestilenceInstance.owner == null) {return false;}
            }
        }
        if(pestilenceInstance.ownerUUID == null){
            pestilenceInstance.ownerUUID = pestilenceInstance.owner.getUuid();
        }
        return true;
    }*/
}
