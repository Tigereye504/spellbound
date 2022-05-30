package net.tigereye.spellbound;

import net.minecraft.util.math.Vec3d;

public interface SpellboundLivingEntity {
    void updatePositionTracker(Vec3d pos);
    Vec3d readPositionTracker();
}
