package net.tigereye.spellbound.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.interfaces.SpellboundLocalDifficulty;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class WorldMixin {

    @Inject(at = @At("RETURN"), method = "getCurrentDifficultyAt")
    public void spellboundGetCurrentDifficultyMixin(BlockPos pos, CallbackInfoReturnable<DifficultyInstance> cir) {
        float modifier = 0;
        for (Player player : ((Level)(Object)this).players()){
            if(pos.closerToCenterThan(player.position(),128)) {
                modifier += SBEnchantmentHelper.getLocalDifficultyModifier((Level) (Object) this, player);
            }
        }
        ((SpellboundLocalDifficulty)cir.getReturnValue()).spellbound$setLocalDifficultyModifier(modifier);
    }

    //@Inject(at = @At("HEAD"), method = "emitGameEvent")
    //public void spellboundWorldEmitGameEventMixin(@Nullable Entity entity, GameEvent gameEvent, BlockPos pos, int range, CallbackInfo info){
    //    if(gameEvent == GameEvent.EQUIP && entity != null){
    //        if(entity instanceof LivingEntity) {
    //            SBEnchantmentHelper.onEquipmentChange((LivingEntity) entity);
    //        }
    //    }
    //}
}
