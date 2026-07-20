package net.tigereye.spellbound.registration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.RelativeMovement;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import java.util.HashSet;
import java.util.Set;

public class SBNetworking {
    public static final ResourceLocation TELEPORT_REQUEST_PACKET_ID = new ResourceLocation(Spellbound.MODID,"teleport_request");
    public static final ResourceLocation GRACE_DATA_PACKET_ID = new ResourceLocation(Spellbound.MODID,"grace_data");
    public static final ResourceLocation REQUEST_STATUS_EFFECT_PACKET_ID = new ResourceLocation(Spellbound.MODID,"status_effect_request");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(TELEPORT_REQUEST_PACKET_ID, (server, client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            server.execute(() -> {
                Set<RelativeMovement> flags = new HashSet<>();
                flags.add(RelativeMovement.X);
                flags.add(RelativeMovement.Y);
                flags.add(RelativeMovement.Z);
                client.connection.teleport(x, y, z, client.getYRot(), client.getXRot(), flags);

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_STATUS_EFFECT_PACKET_ID, (server, client, handler, buf, responseSender) -> {
            int duration = buf.readInt();
            int magnitude = buf.readInt();
            int rawId = buf.readInt();
            server.execute(() -> {
                MobEffect effect = BuiltInRegistries.MOB_EFFECT.byId(rawId);
                if (effect == null) {
                    Spellbound.LOGGER.error("Nonexistant status effect requested by client " + client.getScoreboardName());
                } else {
                    client.addEffect(new MobEffectInstance(effect, duration, magnitude));
                }
            });
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient(){
        ClientPlayNetworking.registerGlobalReceiver((GRACE_DATA_PACKET_ID),(client, handler, buf, responseSender) -> {
            float magnitude = buf.readFloat();
            int ticks = buf.readInt();
            client.execute(() -> {
                if(client.player instanceof SpellboundLivingEntity entity){
                    entity.spellbound$setGraceTicks(ticks);
                    entity.spellbound$setGraceMagnitude(magnitude);
                }
            });
        });
    }
}
