package net.tigereye.spellbound;

import net.fabricmc.api.ClientModInitializer;
import net.tigereye.spellbound.registration.SBGUI;

public class SpellboundClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SBGUI.register();
    }
}
