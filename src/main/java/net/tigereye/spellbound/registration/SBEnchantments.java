package net.tigereye.spellbound.registration;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.*;
import net.tigereye.spellbound.enchantments.damage.*;
import net.tigereye.spellbound.enchantments.efficiency.AccelerationEnchantment;
import net.tigereye.spellbound.enchantments.efficiency.RockCollectingEnchantment;
import net.tigereye.spellbound.enchantments.lure.FisherOfMenEnchantment;
import net.tigereye.spellbound.enchantments.personality.MonogamousEnchantment;
import net.tigereye.spellbound.enchantments.personality.PolygamousEnchantment;
import net.tigereye.spellbound.enchantments.protection.FleshWoundEnchantment;
import net.tigereye.spellbound.enchantments.protection.HeartyEnchantment;
import net.tigereye.spellbound.enchantments.protection.RedAlertEnchantment;
import net.tigereye.spellbound.enchantments.repair.PhotosyntheticEnchantment;
import net.tigereye.spellbound.enchantments.repair.MetabolisingEnchantment;
import net.tigereye.spellbound.enchantments.repair.SelfishEnchantment;
import net.tigereye.spellbound.enchantments.repair.SkotosyntheticEnchantment;
import net.tigereye.spellbound.enchantments.retaliation.OutburstEnchantment;
import net.tigereye.spellbound.enchantments.retaliation.SpikesEnchantment;
import net.tigereye.spellbound.enchantments.unbreaking.SaturatedEnchantment;
import net.tigereye.spellbound.enchantments.repair.LegacyEnchantment;
import net.tigereye.spellbound.enchantments.utility.*;

import java.util.ArrayList;
import java.util.List;

public class SBEnchantments {

    public static List<SBEnchantment> SBEnchantmentList = new ArrayList<>();

    public static final SBEnchantment ACCELERATION = new AccelerationEnchantment();
    public static final SBEnchantment ATTRACTIVE = new AttractiveEnchantment();
    public static final SBEnchantment CAVE_IN = new CaveInEnchantment();
    public static final SBEnchantment DULLNESS = new DullnessEnchantment();
    public static final SBEnchantment FISHER_OF_MEN = new FisherOfMenEnchantment();
    public static final SBEnchantment FLESH_WOUND = new FleshWoundEnchantment();
    public static final SBEnchantment HEARTY = new HeartyEnchantment();
    public static final SBEnchantment SATURATED = new SaturatedEnchantment();
    public static final SBEnchantment SPIKES = new SpikesEnchantment();
    public static final SBEnchantment IMPERSONAL = new ImpersonalEnchantment();
    public static final SBEnchantment JOUSTING = new JoustingEnchantment();
    public static final SBEnchantment LAUNCHING = new LaunchingEnchantment();
    public static final SBEnchantment LEGACY = new LegacyEnchantment();
    public static final SBEnchantment MOUNTED = new MountedEnchantment();
    public static final SBEnchantment MONOGAMOUS = new MonogamousEnchantment();
    public static final SBEnchantment OUTBURST = new OutburstEnchantment();
    public static final SBEnchantment PHASE_LEAP = new PhaseLeapEnchantment();
    public static final SBEnchantment PHASE_STRAFE = new PhaseStrafeEnchantment();
    public static final SBEnchantment PHOTOSYNTHETIC = new PhotosyntheticEnchantment();
    public static final SBEnchantment POLYGAMOUS = new PolygamousEnchantment();
    public static final SBEnchantment PRIMING = new PrimingEnchantment();
    public static final SBEnchantment RAMPAGE = new RampageEnchantment();
    public static final SBEnchantment RED_ALERT = new RedAlertEnchantment();
    public static final SBEnchantment REPULSIVE = new RepulsiveEnchantment();
    public static final SBEnchantment ROCK_COLLECTING = new RockCollectingEnchantment();
    public static final SBEnchantment METABOLISING = new MetabolisingEnchantment();
    public static final SBEnchantment SELFISH = new SelfishEnchantment();
    public static final SBEnchantment SKOTOSYNTHETIC = new SkotosyntheticEnchantment();
    public static final SBEnchantment TETHERING = new TetheringEnchantment();
    public static final SBEnchantment TROPHY_COLLECTING = new TrophyCollectingEnchantment();

    public static void register(){
        register("acceleration", ACCELERATION);
        register("attractive", ATTRACTIVE);
        register("cave_in", CAVE_IN);
        register("dullness", DULLNESS);
        register("fisher_of_men", FISHER_OF_MEN);
        register("flesh_wound", FLESH_WOUND);
        register("hearty", HEARTY);
        register("impersonal", IMPERSONAL);
        register("jousting", JOUSTING);
        register("launching", LAUNCHING);
        register("legacy", LEGACY);
        register("metabolising", METABOLISING);
        register("mounted", MOUNTED);
        register("monogamous", MONOGAMOUS);
        register("outburst", OUTBURST);
        register("phase_leap", PHASE_LEAP);
        register("phase_strafe", PHASE_STRAFE);
        register("photosynthetic", PHOTOSYNTHETIC);
        register("polygamous", POLYGAMOUS);
        register("priming", PRIMING);
        register("rampage", RAMPAGE);
        register("red_alert", RED_ALERT);
        register("repulsive", REPULSIVE);
        register("rock_collecting", ROCK_COLLECTING);
        register("saturated", SATURATED);
        register("selfish", SELFISH);
        register("spikes", SPIKES);
        register("skotosynthetic", SKOTOSYNTHETIC);
        register("tethering", TETHERING);
        register("trophy_collecting", TROPHY_COLLECTING);
    }

    public static void register(String name, SBEnchantment enchantment){
        Registry.register(Registry.ENCHANTMENT,new Identifier(Spellbound.MODID, name), enchantment);
        SBEnchantmentList.add(enchantment);
    }
}
