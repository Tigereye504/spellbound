package net.tigereye.spellbound.mixins.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements SpellboundClientPlayerEntity {
    @Unique
    private boolean jumpReleased = false;

    @Unique
    private boolean hasMidairJumped = false;


    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void SpellboundTickMovementMidairJumpMixin(CallbackInfo info) {
        SBEnchantmentHelper.onMidairJump(this, (ClientPlayerEntity) (Object) this, ((ClientPlayerEntity) (Object) this).input.jumping);
    }

    @Override
    public void spellbound$setJumpReleased(boolean set) {
        jumpReleased = set;
    }
    @Override
    public boolean spellbound$getJumpReleased() {
        return jumpReleased;
    }
    @Override
    public void spellbound$setHasMidairJumped(boolean set) {
        hasMidairJumped = set;
    }
    @Override
    public boolean spellbound$hasMidairJumped() {
        return hasMidairJumped;
    }
}
