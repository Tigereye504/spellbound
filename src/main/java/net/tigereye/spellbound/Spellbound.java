package net.tigereye.spellbound;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.tigereye.spellbound.config.SBConfig;
import net.tigereye.spellbound.registration.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spellbound implements ModInitializer{
    
    public static final String MODID = "spellbound";
    public static final boolean DEBUG = false;
    public static final Logger LOGGER = LogManager.getLogger();
    public static SBConfig config;

    @Override
    public void onInitialize() {
        AutoConfig.register(SBConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(SBConfig.class).getConfig();

        SBItems.register();
        SBEnchantments.register();
        SBStatusEffects.register();
        SBTags.register();
    }
}