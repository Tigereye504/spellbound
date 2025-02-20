package net.tigereye.spellbound;

import net.fabricmc.api.ClientModInitializer;
import net.tigereye.spellbound.registration.SBGUI;
import net.tigereye.spellbound.registration.SBNetworking;
import net.tigereye.spellbound.registration.SBParticles;

public class SpellboundClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SBGUI.register();
        SBNetworking.registerClient();
        SBParticles.registerClient();
    }
}
