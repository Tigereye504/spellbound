package net.tigereye.spellbound.mob_effect.instance;

import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.Optional;
import java.util.UUID;

public class TetheredInstance extends StatusEffectInstance{
    public Entity anchor;
    public UUID tetherUUID;

    public TetheredInstance(Entity anchor, StatusEffect statusEffect) {
        super(statusEffect);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    public TetheredInstance(Entity anchor, int duration) {
        super(SBStatusEffects.TETHERED, duration);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    public TetheredInstance(Entity anchor, int duration, int amplifier) {
        super(SBStatusEffects.TETHERED, duration, amplifier);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    public TetheredInstance(Entity anchor, int duration, int amplifier, boolean ambient, boolean visible) {
        super(SBStatusEffects.TETHERED, duration, amplifier, ambient, visible);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    public TetheredInstance(Entity anchor, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(SBStatusEffects.TETHERED, duration, amplifier, ambient, showParticles, showIcon);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    public TetheredInstance(Entity anchor, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(SBStatusEffects.TETHERED, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    public TetheredInstance(UUID tetherUUID, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(SBStatusEffects.TETHERED, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.tetherUUID = tetherUUID;
    }

    public TetheredInstance(StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        if(statusEffectInstance instanceof TetheredInstance){
            this.anchor = ((TetheredInstance) statusEffectInstance).anchor;
            this.tetherUUID = ((TetheredInstance) statusEffectInstance).tetherUUID;
        }
    }

    public TetheredInstance(Entity anchor, StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.anchor = anchor;
        this.tetherUUID = anchor.getUuid();
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.shouldShowParticles());
        tag.putBoolean("ShowIcon", this.shouldShowIcon());
        if(tetherUUID != null) {
            tag.putUuid("TetherUUID", tetherUUID);
        }
        return tag;
    }

    public static TetheredInstance customFromNbt(NbtCompound tag) {
        int amplifier = tag.getByte("Amplifier");
        int duration = tag.getInt("Duration");
        boolean ambient = tag.getBoolean("Ambient");
        boolean showParticles = true;
        UUID tetherUUID = null;
        if (tag.contains("ShowParticles", 1)) {
            showParticles = tag.getBoolean("ShowParticles");
        }

        boolean showIcon = showParticles;
        if (tag.contains("ShowIcon", 1)) {
            showIcon = tag.getBoolean("ShowIcon");
        }

        if(tag.contains("TetherUUID")){
            tetherUUID = tag.getUuid("TetherUUID");
        }
        return new TetheredInstance(tetherUUID,duration,amplifier,ambient,showParticles,showIcon,null,Optional.empty());
    }
}
