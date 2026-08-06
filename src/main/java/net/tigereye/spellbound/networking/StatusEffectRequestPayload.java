package net.tigereye.spellbound.networking;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.effect.MobEffect;
import net.tigereye.spellbound.Spellbound;

public record StatusEffectRequestPayload (int duration, int magnitude, MobEffect statusEffect) implements CustomPacketPayload{
    
    public static final StreamCodec<FriendlyByteBuf, StatusEffectRequestPayload> STREAM_CODEC = CustomPacketPayload.codec(StatusEffectRequestPayload::write, StatusEffectRequestPayload::new);
    public static final CustomPacketPayload.Type<StatusEffectRequestPayload> TYPE = CustomPacketPayload.createType(Spellbound.MODID+"StatusEffectRequest");

    public StatusEffectRequestPayload(FriendlyByteBuf buf) {
        this(buf.readInt(),buf.readInt(),BuiltInRegistries.MOB_EFFECT.byId(buf.readInt()));
    }

    // Instance method
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(duration);
        buf.writeInt(magnitude);
        buf.writeInt(BuiltInRegistries.MOB_EFFECT.getId(statusEffect));
    }

    // Static method, buffer-first
    public static void write2(FriendlyByteBuf buf, StatusEffectRequestPayload StatusEffectRequest) {
        buf.writeInt(StatusEffectRequest.duration);
        buf.writeInt(StatusEffectRequest.magnitude);
        buf.writeInt(BuiltInRegistries.MOB_EFFECT.getId(StatusEffectRequest.statusEffect));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

}
