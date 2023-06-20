package net.tigereye.spellbound.interfaces;

import net.minecraft.util.math.Vec3d;

public interface SpellboundLivingEntity {
    void updatePositionTracker(Vec3d pos);
    Vec3d readPositionTracker();
    void addNextTickAction(NextTickAction action);
    float getGraceMagnitude();
    int getGraceTicks();
    void setGraceMagnitude(float lastDamageTaken);
    void setGraceTicks(int GraceTicks);
}
