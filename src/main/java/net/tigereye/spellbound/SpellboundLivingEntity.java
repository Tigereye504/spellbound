package net.tigereye.spellbound;

import net.minecraft.util.math.Vec3d;

public interface SpellboundLivingEntity {
    void updateMotionTracker(Vec3d pos);
    Vec3d readMotionTracker();
}
