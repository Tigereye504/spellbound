package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.entity.event.v1.EntitySleepEvents;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.enchantments.damage.*;
import net.tigereye.spellbound.enchantments.efficiency.AccelerationEnchantment;
import net.tigereye.spellbound.enchantments.efficiency.DemolitionEnchantment;
import net.tigereye.spellbound.enchantments.efficiency.RockCollectingEnchantment;
import net.tigereye.spellbound.enchantments.efficiency.WidenedEnchantment;
import net.tigereye.spellbound.enchantments.fortune.ProspectorEnchantment;
import net.tigereye.spellbound.enchantments.looting.DespoilingEnchantment;
import net.tigereye.spellbound.enchantments.looting.ScalpingEnchantment;
import net.tigereye.spellbound.enchantments.lure.FisherOfMenEnchantment;
import net.tigereye.spellbound.enchantments.meta.MonogamousEnchantment;
import net.tigereye.spellbound.enchantments.meta.PolygamousEnchantment;
import net.tigereye.spellbound.enchantments.meta.StoriedEnchantment;
import net.tigereye.spellbound.enchantments.protection.*;
import net.tigereye.spellbound.enchantments.repair.*;
import net.tigereye.spellbound.enchantments.retaliation.OutburstEnchantment;
import net.tigereye.spellbound.enchantments.retaliation.PestilenceEnchantment;
import net.tigereye.spellbound.enchantments.retaliation.SpikesEnchantment;
import net.tigereye.spellbound.enchantments.unbreaking.BufferedEnchantment;
import net.tigereye.spellbound.enchantments.unbreaking.SaturatedEnchantment;
import net.tigereye.spellbound.enchantments.utility.*;
import net.tigereye.spellbound.enchantments.utility.chestplate.AttractiveEnchantment;
import net.tigereye.spellbound.enchantments.utility.chestplate.RepulsiveEnchantment;
import net.tigereye.spellbound.enchantments.utility.chestplate.WarlikeEnchantment;
import net.tigereye.spellbound.enchantments.utility.leggings.HoverEnchantment;
import net.tigereye.spellbound.enchantments.utility.leggings.PhaseLeapEnchantment;
import net.tigereye.spellbound.enchantments.utility.leggings.PhaseStrafeEnchantment;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.ArrayList;
import java.util.List;

public class SBEnchantments {

    public static List<SBEnchantment> SBEnchantmentList = new ArrayList<>();

    public static final SBEnchantment ACCELERATION = new AccelerationEnchantment();
    public static final SBEnchantment AIRLINE = new AirlineEnchantment();
    public static final SBEnchantment ATTRACTIVE = new AttractiveEnchantment();
    public static final SBEnchantment BUFFERED = new BufferedEnchantment();
    public static final SBEnchantment CAVE_IN = new CaveInEnchantment();
    public static final SBEnchantment DEATH_WISH = new DeathWishEnchantment();
    public static final SBEnchantment DEMOLITION = new DemolitionEnchantment();
    public static final SBEnchantment DESPOILING = new DespoilingEnchantment();
    public static final SBEnchantment LAST_GASP = new LastGaspEnchantment();
    public static final SBEnchantment DULLNESS = new DullnessEnchantment();
    public static final SBEnchantment FISHER_OF_MEN = new FisherOfMenEnchantment();
    public static final SBEnchantment FLESH_WOUND = new FleshWoundEnchantment();
    public static final SBEnchantment GRACE = new GraceEnchantment();
    public static final SBEnchantment HEARTY = new HeartyEnchantment();
    public static final SBEnchantment HOVER = new HoverEnchantment();
    public static final SBEnchantment IMPERSONAL = new ImpersonalEnchantment();
    public static final SBEnchantment JOUSTING = new JoustingEnchantment();
    public static final SBEnchantment LAUNCHING = new LaunchingEnchantment();
    public static final SBEnchantment LEGACY = new LegacyEnchantment();
    public static final SBEnchantment METABOLISING = new MetabolisingEnchantment();
    public static final SBEnchantment MOUNTED = new MountedEnchantment();
    public static final SBEnchantment MONOGAMOUS = new MonogamousEnchantment();
    public static final SBEnchantment OUTBURST = new OutburstEnchantment();
    public static final SBEnchantment PESTILENCE = new PestilenceEnchantment();
    public static final SBEnchantment PHASE_LEAP = new PhaseLeapEnchantment();
    public static final SBEnchantment PHASE_STRAFE = new PhaseStrafeEnchantment();
    public static final SBEnchantment PHOTOSYNTHETIC = new PhotosyntheticEnchantment();
    public static final SBEnchantment POLYGAMOUS = new PolygamousEnchantment();
    public static final SBEnchantment PRIMING = new PrimingEnchantment();
    public static final SBEnchantment PROSPECTOR = new ProspectorEnchantment();
    public static final SBEnchantment RAMPAGE = new RampageEnchantment();
    public static final SBEnchantment RED_ALERT = new RedAlertEnchantment();
    public static final SBEnchantment REPULSIVE = new RepulsiveEnchantment();
    public static final SBEnchantment RESURFACING = new ResurfacingEnchantment();
    public static final RockCollectingEnchantment ROCK_COLLECTING = new RockCollectingEnchantment();
    public static final SBEnchantment SATURATED = new SaturatedEnchantment();
    public static final SBEnchantment SCALPING = new ScalpingEnchantment();
    public static final SBEnchantment SELFISH = new SelfishEnchantment();
    public static final SBEnchantment SKOTOSYNTHETIC = new SkotosyntheticEnchantment();
    public static final SBEnchantment SPIKES = new SpikesEnchantment();
    public static final SBEnchantment STORIED = new StoriedEnchantment();
    public static final SBEnchantment TETHERING = new TetheringEnchantment();
    public static final TrophyCollectingEnchantment TROPHY_COLLECTING = new TrophyCollectingEnchantment();
    public static final WarlikeEnchantment WARLIKE = new WarlikeEnchantment();
    public static final WidenedEnchantment WIDENED = new WidenedEnchantment();

    public static void register(){
        register("acceleration", ACCELERATION);
        register("airline", AIRLINE);
        register("attractive", ATTRACTIVE);
        register("buffered", BUFFERED);
        register("cave_in", CAVE_IN);
        register("death_wish", DEATH_WISH);
        register("demolition", DEMOLITION);
        register("despoiling", DESPOILING);
        register("dullness", DULLNESS);
        register("fisher_of_men", FISHER_OF_MEN);
        register("flesh_wound", FLESH_WOUND);
        register("grace", GRACE);
        register("hearty", HEARTY);
        register("hover", HOVER);
        register("impersonal", IMPERSONAL);
        register("jousting", JOUSTING);
        register("last_gasp", LAST_GASP);
        register("launching", LAUNCHING);
        register("legacy", LEGACY);
        register("metabolising", METABOLISING);
        register("mounted", MOUNTED);
        register("monogamous", MONOGAMOUS);
        register("outburst", OUTBURST);
        register("pestilence", PESTILENCE);
        register("phase_leap", PHASE_LEAP);
        register("phase_strafe", PHASE_STRAFE);
        register("photosynthetic", PHOTOSYNTHETIC);
        register("polygamous", POLYGAMOUS);
        register("priming", PRIMING);
        register("prospector", PROSPECTOR);
        register("rampage", RAMPAGE);
        register("red_alert", RED_ALERT);
        register("repulsive", REPULSIVE);
        register("resurfacing", RESURFACING);
        register("rock_collecting", ROCK_COLLECTING);
        register("saturated", SATURATED);
        register("scalping", SCALPING);
        register("selfish", SELFISH);
        register("spikes", SPIKES);
        register("storied", STORIED);
        register("skotosynthetic", SKOTOSYNTHETIC);
        register("tethering", TETHERING);
        register("trophy_collecting", TROPHY_COLLECTING);
        register("warlike", WARLIKE);
        register("widened", WIDENED);

        EntitySleepEvents.START_SLEEPING.register(((entity, sleepingPos) -> SBEnchantmentHelper.onStartSleeping(entity)));
    }

    public static void register(String name, SBEnchantment enchantment){
        Registry.register(Registries.ENCHANTMENT,new Identifier(Spellbound.MODID, name), enchantment);
        SBEnchantmentList.add(enchantment);
    }
}
