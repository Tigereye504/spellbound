package net.tigereye.spellbound.interfaces;

import net.minecraft.util.math.Vec3d;

public interface SpellboundLivingEntity {
    void spellbound$updatePositionTracker(Vec3d pos);
    Vec3d spellbound$readPositionTracker();
    void spellbound$addNextTickAction(NextTickAction action);
    float spellbound$getGraceMagnitude();
    int spellbound$getGraceTicks();
    void spellbound$setGraceMagnitude(float lastDamageTaken);
    void spellbound$setGraceTicks(int GraceTicks);
}
