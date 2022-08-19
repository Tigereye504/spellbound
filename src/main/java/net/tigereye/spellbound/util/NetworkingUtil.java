package net.tigereye.spellbound.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBNetworking;

public class NetworkingUtil {

    public static void sendTeleportRequestPacket(Vec3d pos){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());
        ClientPlayNetworking.send(SBNetworking.TELEPORT_REQUEST_PACKET_ID,buf);
    }
}
