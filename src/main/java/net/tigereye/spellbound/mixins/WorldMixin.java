package net.tigereye.spellbound.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.tigereye.spellbound.interfaces.SpellboundLocalDifficulty;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public abstract class WorldMixin {

    @Inject(at = @At("RETURN"), method = "getLocalDifficulty")
    public void spellboundWorldEmitGameEventMixin(BlockPos pos, CallbackInfoReturnable<LocalDifficulty> cir) {
        float modifier = 0;
        for (PlayerEntity player : ((World)(Object)this).getPlayers()){
            if(pos.isWithinDistance(player.getPos(),128)) {
                modifier += SBEnchantmentHelper.getLocalDifficultyModifier((World) (Object) this, player);
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
