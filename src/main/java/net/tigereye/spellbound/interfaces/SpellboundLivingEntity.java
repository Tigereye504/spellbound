package net.tigereye.spellbound.interfaces;

import net.minecraft.util.math.Vec3d;

public interface SpellboundLivingEntity {
    void updatePositionTracker(Vec3d pos);
    Vec3d readPositionTracker();
    public void setDurabilityBuffer(float buffer);
    public float getDurabilityBuffer();
    public void setMaxDurabilityBuffer(float buffer);
    public float getMaxDurabilityBuffer();
}
