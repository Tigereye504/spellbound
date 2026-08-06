package net.tigereye.spellbound.interfaces;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.minecraft.world.phys.Vec3;

public interface SpellboundLivingEntity {
    void spellbound$updatePositionTracker(Vec3 pos);
    Vec3 spellbound$readPositionTracker();
    void spellbound$addDelayedAction(DelayedAction action);
    List<DelayedAction> spellbound$getDelayedActions();
    float spellbound$getGraceMagnitude();
    int spellbound$getGraceTicks();
    void spellbound$setGraceMagnitude(float lastDamageTaken);
    void spellbound$setGraceTicks(int GraceTicks);
    boolean spellbound$shouldDisplayShielded();
    void spellbound$setLastPrimer(UUID uuid);
    UUID spellbound$getLastPrimer();
    void spellbound$setLastTether(UUID uuid);
    UUID spellbound$setLastTether();
}
