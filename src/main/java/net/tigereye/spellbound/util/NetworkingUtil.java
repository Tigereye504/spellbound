package net.tigereye.spellbound.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.registration.SBNetworking;

public class NetworkingUtil {

    public static void sendTeleportRequestPacket(Vec3d pos){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(pos.getX());
        buf.writeDouble(pos.getY());
        buf.writeDouble(pos.getZ());
        ClientPlayNetworking.send(SBNetworking.TELEPORT_REQUEST_PACKET_ID,buf);
    }

    public static void sendStatusEffectRequestPacket(int duration, int magnitude, StatusEffect statusEffect){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(duration);
        buf.writeInt(magnitude);
        buf.writeInt(StatusEffect.getRawId(statusEffect));
        ClientPlayNetworking.send(SBNetworking.REQUEST_STATUS_EFFECT_PACKET_ID,buf);
    }

    public static void sendGraceDataPacket(float magnitude, int ticks, ServerPlayerEntity player){
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(magnitude);
        buf.writeInt(ticks);
        ServerPlayNetworking.send(player,SBNetworking.GRACE_DATA_PACKET_ID,buf);
    }
}
