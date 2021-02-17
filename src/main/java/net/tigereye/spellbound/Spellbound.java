package net.tigereye.spellbound;

import net.fabricmc.api.ModInitializer;
import net.tigereye.spellbound.registration.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spellbound implements ModInitializer{
    
    public static final String MODID = "spellbound";
    public static final boolean DEBUG = false;
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        SBEnchantments.register();
        SBStatusEffects.register();
    }
}