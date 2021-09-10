package net.tigereye.spellbound.registration;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.*;

import java.util.ArrayList;
import java.util.List;

public class SBEnchantments {

    public static List<SBEnchantment> SBEnchantmentList = new ArrayList<>();

    public static final SBEnchantment ATTRACTIVE = new AttractiveEnchantment();
    public static final SBEnchantment CAVE_IN = new CaveInEnchantment();
    public static final SBEnchantment DULLNESS = new DullnessEnchantment();
    public static final SBEnchantment FLESH_WOUND = new FleshWoundEnchantment();
    public static final SBEnchantment HEARTY = new HeartyEnchantment();
    public static final SBEnchantment IMPERSONAL = new ImpersonalEnchantment();
    public static final SBEnchantment JOUSTING = new JoustingEnchantment();
    public static final SBEnchantment LAUNCHING = new LaunchingEnchantment();
    public static final SBEnchantment LEGACY = new LegacyEnchantment();
    public static final SBEnchantment MOUNTED = new MountedEnchantment();
    public static final SBEnchantment MONOGAMOUS = new MonogamousEnchantment();
    public static final SBEnchantment PHASE_LEAP = new PhaseLeapEnchantment();
    public static final SBEnchantment PHASE_STRAFE = new PhaseStrafeEnchantment();
    public static final SBEnchantment PHOTOSYNTHETIC = new PhotosyntheticEnchantment();
    public static final SBEnchantment POLYGAMOUS = new PolygamousEnchantment();
    public static final SBEnchantment PRIMING = new PrimingEnchantment();
    public static final SBEnchantment RAMPAGE = new RampageEnchantment();
    public static final SBEnchantment RED_ALERT = new RedAlertEnchantment();
    public static final SBEnchantment REPULSIVE = new RepulsiveEnchantment();
    public static final SBEnchantment ROCK_COLLECTING = new RockCollectorEnchantment();
    public static final SBEnchantment SELFISH = new SelfishEnchantment();
    public static final SBEnchantment SKOTOSYNTHETIC = new SkotosyntheticEnchantment();
    public static final SBEnchantment TETHERING = new TetheringEnchantment();
    public static final SBEnchantment TROPHY_COLLECTING = new TrophyCollectorEnchantment();

    public static void register(){
        register("attractive", ATTRACTIVE);
        register("cave_in", CAVE_IN);
        register("dullness", DULLNESS);
        register("flesh_wound", FLESH_WOUND);
        register("hearty", HEARTY);
        register("impersonal", IMPERSONAL);
        register("jousting", JOUSTING);
        register("launching", LAUNCHING);
        register("legacy", LEGACY);
        register("mounted", MOUNTED);
        register("monogamous", MONOGAMOUS);
        register("phase_leap", PHASE_LEAP);
        register("phase_strafe", PHASE_STRAFE);
        register("photosynthetic", PHOTOSYNTHETIC);
        register("polygamous", POLYGAMOUS);
        register("priming", PRIMING);
        register("rampage", RAMPAGE);
        register("repulsive", REPULSIVE);
        register("rock_collecting", ROCK_COLLECTING);
        register("red_alert", RED_ALERT);
        register("selfish", SELFISH);
        register("skotosynthetic", SKOTOSYNTHETIC);
        register("tethering", TETHERING);
        register("trophy_collecting", TROPHY_COLLECTING);
    }

    public static void register(String name, SBEnchantment enchantment){
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, name), enchantment);
        SBEnchantmentList.add(enchantment);
    }
}
