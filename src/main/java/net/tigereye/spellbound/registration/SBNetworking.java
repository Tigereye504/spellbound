package net.tigereye.spellbound.registration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.RelativeMovement;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.networking.GraceDataPayload;
import net.tigereye.spellbound.networking.StatusEffectRequestPayload;
import net.tigereye.spellbound.networking.TeleportRequestPayload;

import java.util.HashSet;
import java.util.Set;

public class SBNetworking {

    public static void register() {
        PayloadTypeRegistry.playC2S().register(TeleportRequestPayload.TYPE, TeleportRequestPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TeleportRequestPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                Set<RelativeMovement> flags = new HashSet<>();
                flags.add(RelativeMovement.X);
                flags.add(RelativeMovement.Y);
                flags.add(RelativeMovement.Z);
                context.player().connection.teleport(payload.destination().x, payload.destination().y, payload.destination().z,
                        context.player().getYRot(), context.player().getXRot(), flags);
            });
        });

        PayloadTypeRegistry.playC2S().register(StatusEffectRequestPayload.TYPE, StatusEffectRequestPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(StatusEffectRequestPayload.TYPE, (payload,context) -> {
            context.server().execute(() -> {
                if (payload.statusEffect() == null) {
                    Spellbound.LOGGER.error("Nonexistant status effect requested by client " + context.player().getScoreboardName());
                } else {
                    context.player().addEffect(new MobEffectInstance(Holder.direct(payload.statusEffect()), payload.duration(), payload.magnitude()));
                }
            });
        });
        
        PayloadTypeRegistry.playS2C().register(GraceDataPayload.TYPE, GraceDataPayload.STREAM_CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient(){
        ClientPlayNetworking.registerGlobalReceiver(GraceDataPayload.TYPE,(payload, context) -> {
            context.client().execute(() -> {
                if(context.client().player instanceof SpellboundLivingEntity entity){
                    entity.spellbound$setGraceTicks(payload.ticks());
                    entity.spellbound$setGraceMagnitude(payload.magnitude());
                }
            });
        });
    }
}
