package net.tigereye.spellbound.registration;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.*;

public class SBEnchantments {

    public static final Enchantment CAVE_IN = new CaveInEnchantment();
    public static final Enchantment DULLNESS = new DullnessEnchantment();
    public static final Enchantment IMPERSONAL = new ImpersonalEnchantment();
    public static final Enchantment JOUSTING = new JoustingEnchantment();
    public static final Enchantment LAUNCHING = new LaunchingEnchantment();
    public static final Enchantment LEGACY = new LegacyEnchantment();
    //public static final Enchantment MOUNTED = new MountedEnchantment();
    public static final Enchantment MONOGAMOUS = new MonogamousEnchantment();
    public static final Enchantment POLYGAMOUS = new PolygamousEnchantment();
    public static final Enchantment PRIMING = new PrimingEnchantment();
    public static final Enchantment RAMPAGE = new RampageEnchantment();
    public static final Enchantment ROCK_COLLECTING = new RockCollectorEnchantment();
    public static final Enchantment RED_ALERT = new RedAlertEnchantment();
    public static final Enchantment SELFISH = new SelfishEnchantment();
    public static final Enchantment TETHERING = new TetheringEnchantment();
    public static final Enchantment TROPHY_COLLECTING = new TrophyCollectorEnchantment();

    public static void register(){
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "cave_in"), CAVE_IN);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "dullness"), DULLNESS);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "impersonal"), IMPERSONAL);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "jousting"), JOUSTING);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "launching"), LAUNCHING);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "legacy"), LEGACY);
        //Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "mounted"), MOUNTED);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "monogamous"), MONOGAMOUS);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "polygamous"), POLYGAMOUS);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "priming"), PRIMING);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "rampage"), RAMPAGE);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "rock_collecting"), ROCK_COLLECTING);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "red_alert"), RED_ALERT);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "selfish"), SELFISH);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "tethering"), TETHERING);
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, "trophy_collecting"), TROPHY_COLLECTING);
    }
}
