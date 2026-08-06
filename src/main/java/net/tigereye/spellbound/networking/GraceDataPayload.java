package net.tigereye.spellbound.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.tigereye.spellbound.Spellbound;

public record GraceDataPayload (float magnitude, int ticks) implements CustomPacketPayload{
    
    public static final StreamCodec<FriendlyByteBuf, GraceDataPayload> STREAM_CODEC = CustomPacketPayload.codec(GraceDataPayload::write, GraceDataPayload::new);
    public static final CustomPacketPayload.Type<GraceDataPayload> TYPE = CustomPacketPayload.createType(Spellbound.MODID+"GraceData");

    public GraceDataPayload(FriendlyByteBuf buf) {
        this(buf.readFloat(),buf.readInt());
    }

    // Instance method
    public void write(FriendlyByteBuf buf) {
        buf.writeDouble(magnitude);
        buf.writeDouble(ticks);
    }

    // Static method, buffer-first
    public static void write2(FriendlyByteBuf buf, GraceDataPayload GraceDataPacket) {
        buf.writeDouble(GraceDataPacket.magnitude);
        buf.writeDouble(GraceDataPacket.ticks);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
