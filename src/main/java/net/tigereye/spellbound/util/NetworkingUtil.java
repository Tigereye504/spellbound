package net.tigereye.spellbound.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.registration.SBNetworking;

public class NetworkingUtil {

    public static void sendTeleportRequestPacket(Vec3 pos){
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(pos.x());
        buf.writeDouble(pos.y());
        buf.writeDouble(pos.z());
        ClientPlayNetworking.send(SBNetworking.TELEPORT_REQUEST_PACKET_ID,buf);
    }

    public static void sendStatusEffectRequestPacket(int duration, int magnitude, MobEffect statusEffect){
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(duration);
        buf.writeInt(magnitude);
        buf.writeInt(MobEffect.getId(statusEffect));
        ClientPlayNetworking.send(SBNetworking.REQUEST_STATUS_EFFECT_PACKET_ID,buf);
    }

    public static void sendGraceDataPacket(float magnitude, int ticks, ServerPlayer player){
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(magnitude);
        buf.writeInt(ticks);
        ServerPlayNetworking.send(player,SBNetworking.GRACE_DATA_PACKET_ID,buf);
    }
}
