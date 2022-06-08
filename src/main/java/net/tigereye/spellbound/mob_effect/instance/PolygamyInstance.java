package net.tigereye.spellbound.mob_effect.instance;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.nbt.NbtCompound;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.Optional;
import java.util.UUID;

public class PolygamyInstance extends StatusEffectInstance{
    public UUID itemUUID;

    public PolygamyInstance(UUID itemUUID, StatusEffect statusEffect) {
        super(statusEffect);
        this.itemUUID = itemUUID;
    }

    public PolygamyInstance(UUID itemUUID, int duration) {
        super(SBStatusEffects.POLYGAMY, duration);
        this.itemUUID = itemUUID;
    }

    public PolygamyInstance(UUID itemUUID, int duration, int amplifier) {
        super(SBStatusEffects.POLYGAMY, duration, amplifier);
        this.itemUUID = itemUUID;
    }

    public PolygamyInstance(UUID itemUUID, int duration, int amplifier, boolean ambient, boolean visible) {
        super(SBStatusEffects.POLYGAMY, duration, amplifier, ambient, visible);
        this.itemUUID = itemUUID;
    }

    public PolygamyInstance(UUID itemUUID, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon) {
        super(SBStatusEffects.POLYGAMY, duration, amplifier, ambient, showParticles, showIcon);
        this.itemUUID = itemUUID;
    }

    public PolygamyInstance(UUID itemUUID, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, StatusEffectInstance hiddenEffect, Optional<FactorCalculationData> factorCalculationData) {
        super(SBStatusEffects.POLYGAMY, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.itemUUID = itemUUID;
    }


    public PolygamyInstance(StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        if(statusEffectInstance instanceof PolygamyInstance){
            this.itemUUID = ((PolygamyInstance) statusEffectInstance).itemUUID;
        }
    }

    public PolygamyInstance(UUID itemUUID, StatusEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.itemUUID = itemUUID;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putByte("Id", (byte)StatusEffect.getRawId(this.getEffectType()));
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.shouldShowParticles());
        tag.putBoolean("ShowIcon", this.shouldShowIcon());
        tag.putUuid("ItemUUID", itemUUID);
        return tag;
    }

    public static PolygamyInstance customFromNbt(NbtCompound tag) {
        int amplifier = tag.getByte("Amplifier");
        int duration = tag.getInt("Duration");
        boolean ambient = tag.getBoolean("Ambient");
        boolean showParticles = true;
        UUID tetherUUID;
        if (tag.contains("ShowParticles", 1)) {
            showParticles = tag.getBoolean("ShowParticles");
        }

        boolean showIcon = showParticles;
        if (tag.contains("ShowIcon", 1)) {
            showIcon = tag.getBoolean("ShowIcon");
        }

        if(tag.contains("ItemUUID")){
            tetherUUID = tag.getUuid("ItemUUID");
        }
        else{
            tetherUUID = new UUID(0,0);
        }
        return new PolygamyInstance(tetherUUID,duration,amplifier,ambient,showParticles,showIcon,null,Optional.empty());
    }
}
