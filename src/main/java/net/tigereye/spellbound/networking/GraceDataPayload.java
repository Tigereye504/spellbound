package net.tigereye.spellbound.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.tigereye.spellbound.Spellbound;
import org.jetbrains.annotations.NotNull;

public record GraceDataPayload (float magnitude, int ticks) implements CustomPacketPayload{

    public static final ResourceLocation ID = new ResourceLocation(Spellbound.MODID, "grace_data");
    public static final StreamCodec<FriendlyByteBuf, GraceDataPayload> STREAM_CODEC = CustomPacketPayload.codec(GraceDataPayload::write, GraceDataPayload::new);
    public static final CustomPacketPayload.Type<GraceDataPayload> TYPE = CustomPacketPayload.createType(ID.toString());

    public GraceDataPayload(FriendlyByteBuf buf) {
        this(buf.readFloat(),buf.readInt());
    }

    // Instance method
    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(magnitude);
        buf.writeInt(ticks);
    }

    // Static method, buffer-first
    public static void write2(FriendlyByteBuf buf, GraceDataPayload GraceDataPacket) {
        buf.writeFloat(GraceDataPacket.magnitude);
        buf.writeInt(GraceDataPacket.ticks);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
