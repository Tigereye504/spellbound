package net.tigereye.spellbound;

import net.fabricmc.api.ModInitializer;
import net.tigereye.spellbound.registration.*;

public class Spellbound implements ModInitializer{
    
    public static final String MODID = "spellbound";
    public static final boolean DEBUG = false;

    @Override
    public void onInitialize() {
        SBEnchantments.register();
        SBStatusEffects.register();
    }
}