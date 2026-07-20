package net.tigereye.spellbound.mob_effect.instance;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class OwnedStatusEffectInstance extends MobEffectInstance{
    public Entity owner = null;
    public UUID ownerUUID = null;

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffect statusEffect) {
        super(statusEffect);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffect statusEffect, int duration) {
        super(statusEffect, duration);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffect statusEffect, int duration, int amplifier) {
        super(statusEffect, duration, amplifier);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffect statusEffect, int duration, int amplifier, boolean ambient, boolean visible) {
        super(statusEffect, duration, amplifier, ambient, visible);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, MobEffectInstance hiddenEffect, Optional<FactorData> factorCalculationData) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    public OwnedStatusEffectInstance(UUID ownerUUID, MobEffect statusEffect, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, MobEffectInstance hiddenEffect, Optional<FactorData> factorCalculationData) {
        super(statusEffect, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.ownerUUID = ownerUUID;
    }

    public OwnedStatusEffectInstance(MobEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        if(statusEffectInstance instanceof OwnedStatusEffectInstance){
            this.owner = ((OwnedStatusEffectInstance) statusEffectInstance).owner;
            if(owner != null) {
                this.ownerUUID = owner.getUUID();
            }
        }
    }

    public OwnedStatusEffectInstance(@Nullable Entity owner, MobEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.owner = owner;
        if(owner != null) {
            this.ownerUUID = owner.getUUID();
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putByte("Id", (byte)BuiltInRegistries.MOB_EFFECT.getId(getEffect()));
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.isVisible());
        tag.putBoolean("ShowIcon", this.showIcon());
        if(ownerUUID != null) {
            tag.putUUID("OwnerUUID", ownerUUID);
        }
        return tag;
    }

    public static OwnedStatusEffectInstance customFromNbt(MobEffect type, CompoundTag tag) {
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
            ownerUUID = tag.getUUID("OwnerUUID");
        }
        return new OwnedStatusEffectInstance(ownerUUID,type,duration,amplifier,ambient,showParticles,showIcon,null,Optional.empty());
    }

    public boolean fillMissingOwnerData(ServerLevel world){
        if(this.owner != null && this.ownerUUID != null){
            return true;
        }
        if(this.owner == null && this.ownerUUID == null){
            return false;
        }
        if(this.owner == null){
            if(world == null){
                return false;
            }
            this.owner = world.getEntity(this.ownerUUID);
            if(this.owner == null) {return false;}
        }
        if(this.ownerUUID == null){
            this.ownerUUID = this.owner.getUUID();
        }
        return true;
    }

    public boolean fillMissingOwnerData(Entity entity){
        Level world = entity.level();
        if(world instanceof ServerLevel sWorld){
            return fillMissingOwnerData(sWorld);
        }
        else{
            return fillMissingOwnerData((ServerLevel) null);
        }
    }
}
