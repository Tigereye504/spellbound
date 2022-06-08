package net.tigereye.spellbound.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(World.class)
public class WorldMixin {


    //@Inject(at = @At("HEAD"), method = "emitGameEvent")
    //public void spellboundWorldEmitGameEventMixin(@Nullable Entity entity, GameEvent gameEvent, BlockPos pos, int range, CallbackInfo info){
    //    if(gameEvent == GameEvent.EQUIP && entity != null){
    //        if(entity instanceof LivingEntity) {
    //            SBEnchantmentHelper.onEquipmentChange((LivingEntity) entity);
    //        }
    //    }
    //}
}
