package net.tigereye.spellbound.interfaces;

public interface SpellboundClientPlayerEntity {
    void setJumpReleased(boolean set);
    boolean getJumpReleased();
    void setHasMidairJumped(boolean set);
    boolean hasMidairJumped();
}
