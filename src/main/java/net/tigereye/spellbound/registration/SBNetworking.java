package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;

public class SBNetworking {
    public static final Identifier TELEPORT_REQUEST_PACKET_ID = new Identifier(Spellbound.MODID,"teleport_request");

    public static void register(){
        ServerPlayNetworking.registerGlobalReceiver(TELEPORT_REQUEST_PACKET_ID, (server, client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            server.execute(() -> client.requestTeleportAndDismount(x,y,z));
        });
    }
}
