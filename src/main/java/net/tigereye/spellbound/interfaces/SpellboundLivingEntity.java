package net.tigereye.spellbound.interfaces;

import net.minecraft.util.math.Vec3d;

import java.util.List;

public interface SpellboundLivingEntity {
    void spellbound$updatePositionTracker(Vec3d pos);
    Vec3d spellbound$readPositionTracker();
    void spellbound$addDelayedAction(DelayedAction action);
    List<DelayedAction> spellbound$getDelayedActions();
    float spellbound$getGraceMagnitude();
    int spellbound$getGraceTicks();
    void spellbound$setGraceMagnitude(float lastDamageTaken);
    void spellbound$setGraceTicks(int GraceTicks);
    boolean spellbound$shouldDisplayShielded();
}
