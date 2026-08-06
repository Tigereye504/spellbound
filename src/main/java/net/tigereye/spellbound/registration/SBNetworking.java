package net.tigereye.spellbound.registration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.networking.GraceDataPayload;
import net.tigereye.spellbound.networking.StatusEffectRequestPayload;
import net.tigereye.spellbound.networking.TeleportRequestPayload;
public class SBNetworking {
    public static final ResourceLocation TELEPORT_REQUEST_PACKET_ID = new ResourceLocation(Spellbound.MODID,"teleport_request");
    public static final ResourceLocation GRACE_DATA_PACKET_ID = new ResourceLocation(Spellbound.MODID,"grace_data");
    public static final ResourceLocation REQUEST_STATUS_EFFECT_PACKET_ID = new ResourceLocation(Spellbound.MODID,"status_effect_request");
    

    public static void register() {
        PayloadTypeRegistry.playC2S().register(TeleportRequestPayload.TYPE, TeleportRequestPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(TeleportRequestPayload.TYPE, (payload, context) -> {
            context.server().execute(() -> {
                context.player().teleportTo(payload.destination().x, payload.destination().y, payload.destination().z);
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
