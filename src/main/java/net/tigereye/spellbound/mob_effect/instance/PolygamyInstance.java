package net.tigereye.spellbound.mob_effect.instance;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.Optional;
import java.util.UUID;

public class PolygamyInstance extends MobEffectInstance{
    public UUID itemUUID;

    public PolygamyInstance(UUID itemUUID, MobEffect statusEffect) {
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

    public PolygamyInstance(UUID itemUUID, int duration, int amplifier, boolean ambient, boolean showParticles, boolean showIcon, MobEffectInstance hiddenEffect, Optional<FactorData> factorCalculationData) {
        super(SBStatusEffects.POLYGAMY, duration, amplifier, ambient, showParticles, showIcon, hiddenEffect, factorCalculationData);
        this.itemUUID = itemUUID;
    }


    public PolygamyInstance(MobEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        if(statusEffectInstance instanceof PolygamyInstance){
            this.itemUUID = ((PolygamyInstance) statusEffectInstance).itemUUID;
        }
    }

    public PolygamyInstance(UUID itemUUID, MobEffectInstance statusEffectInstance) {
        super(statusEffectInstance);
        this.itemUUID = itemUUID;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putByte("Id", (byte)MobEffect.getId(this.getEffect()));
        tag.putByte("Amplifier", (byte)this.getAmplifier());
        tag.putInt("Duration", this.getDuration());
        tag.putBoolean("Ambient", this.isAmbient());
        tag.putBoolean("ShowParticles", this.isVisible());
        tag.putBoolean("ShowIcon", this.showIcon());
        tag.putUUID("ItemUUID", itemUUID);
        return tag;
    }

    public static PolygamyInstance customFromNbt(CompoundTag tag) {
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
            tetherUUID = tag.getUUID("ItemUUID");
        }
        else{
            tetherUUID = new UUID(0,0);
        }
        return new PolygamyInstance(tetherUUID,duration,amplifier,ambient,showParticles,showIcon,null,Optional.empty());
    }
}
