package net.tigereye.spellbound.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.networking.GraceDataPayload;
import net.tigereye.spellbound.networking.StatusEffectRequestPayload;
import net.tigereye.spellbound.networking.TeleportRequestPayload;

public class NetworkingUtil {


    public static void sendTeleportRequestPacket(Vec3 pos){
        ClientPlayNetworking.send(new TeleportRequestPayload(pos));
    }

    public static void sendStatusEffectRequestPacket(int duration, int magnitude, MobEffect statusEffect){
        ClientPlayNetworking.send(new StatusEffectRequestPayload(duration, magnitude, statusEffect));
    }

    public static void sendGraceDataPacket(float magnitude, int ticks, ServerPlayer player){
        ServerPlayNetworking.send(player,new GraceDataPayload(magnitude, ticks));
    }
}
