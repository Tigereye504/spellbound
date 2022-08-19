package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;

import java.util.HashSet;
import java.util.Set;

public class SBNetworking {
    public static final Identifier TELEPORT_REQUEST_PACKET_ID = new Identifier(Spellbound.MODID,"teleport_request");

    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(TELEPORT_REQUEST_PACKET_ID, (server, client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            server.execute(() -> {
                Set<PlayerPositionLookS2CPacket.Flag> flags = new HashSet<>();
                flags.add(PlayerPositionLookS2CPacket.Flag.X);
                flags.add(PlayerPositionLookS2CPacket.Flag.Y);
                flags.add(PlayerPositionLookS2CPacket.Flag.Z);
                client.networkHandler.requestTeleport(x,y,z, client.getYaw(), client.getPitch(), flags);

            });
        });
    }
}
