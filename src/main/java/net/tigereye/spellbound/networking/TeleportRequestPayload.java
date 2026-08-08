package net.tigereye.spellbound.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;

public record TeleportRequestPayload (Vec3 destination) implements CustomPacketPayload{
    
    public static final StreamCodec<FriendlyByteBuf, TeleportRequestPayload> STREAM_CODEC = CustomPacketPayload.codec(TeleportRequestPayload::write, TeleportRequestPayload::new);
    public static final CustomPacketPayload.Type<TeleportRequestPayload> TYPE = CustomPacketPayload.createType(Spellbound.MODID+":teleport_request");

    public TeleportRequestPayload(FriendlyByteBuf buf) {
        this(new Vec3(buf.readDouble(),buf.readDouble(),buf.readDouble()));
    }

    // Instance method
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(destination.x);
        buf.writeDouble(destination.y);
        buf.writeDouble(destination.z);
    }

    // Static method, buffer-first
    public static void write2(FriendlyByteBuf buf, TeleportRequestPayload teleportRequest) {
        buf.writeDouble(teleportRequest.destination.x);
        buf.writeDouble(teleportRequest.destination.y);
        buf.writeDouble(teleportRequest.destination.z);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
