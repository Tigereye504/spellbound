package net.tigereye.spellbound.registration;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import java.util.HashSet;
import java.util.Set;

public class SBNetworking {
    public static final Identifier TELEPORT_REQUEST_PACKET_ID = new Identifier(Spellbound.MODID,"teleport_request");
    public static final Identifier GRACE_DATA_PACKET_ID = new Identifier(Spellbound.MODID,"grace_data");
    public static final Identifier REQUEST_STATUS_EFFECT_PACKET_ID = new Identifier(Spellbound.MODID,"status_effect_request");

    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(TELEPORT_REQUEST_PACKET_ID, (server, client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            server.execute(() -> {
                Set<PlayerPositionLookS2CPacket.Flag> flags = new HashSet<>();
                flags.add(PlayerPositionLookS2CPacket.Flag.X);
                flags.add(PlayerPositionLookS2CPacket.Flag.Y);
                flags.add(PlayerPositionLookS2CPacket.Flag.Z);
                client.networkHandler.requestTeleport(x, y, z, client.getYaw(), client.getPitch(), flags);

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(REQUEST_STATUS_EFFECT_PACKET_ID, (server, client, handler, buf, responseSender) -> {
            int duration = buf.readInt();
            int magnitude = buf.readInt();
            int rawId = buf.readInt();
            server.execute(() -> {
                StatusEffect effect = StatusEffect.byRawId(rawId);
                if (effect == null) {
                    Spellbound.LOGGER.error("Nonexistant status effect requested by client " + client.getEntityName());
                } else {
                    client.addStatusEffect(new StatusEffectInstance(effect, duration, magnitude));
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
                    entity.setGraceTicks(ticks);
                    entity.setGraceMagnitude(magnitude);
                }
            });
        });
    }
}
