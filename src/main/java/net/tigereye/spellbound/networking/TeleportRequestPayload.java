package net.tigereye.spellbound.networking;

import java.util.UUID;

import net.fabricmc.fabric.impl.networking.NetworkingImpl;
import net.fabricmc.fabric.impl.networking.RegistrationPayload;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.phys.Vec3;

public record TeleportRequestPayload(Vec3 destination) implements CustomPacketPayload{
	public static final CustomPacketPayload.Type<RegistrationPayload> REGISTER = new CustomPacketPayload.Type<>(NetworkingImpl.REGISTER_CHANNEL);
	public static final StreamCodec<FriendlyByteBuf, RegistrationPayload> REGISTER_CODEC = codec(REGISTER);
    
    @Override
    public Type<? extends CustomPacketPayload> type() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'type'");
    }
    
}
