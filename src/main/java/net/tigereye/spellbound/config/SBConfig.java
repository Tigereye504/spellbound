package net.tigereye.spellbound.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.tigereye.spellbound.Spellbound;

@Config(name = Spellbound.MODID)
public class SBConfig implements ConfigData {

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public AccelerationConfig acceleration = new AccelerationConfig();
    public static class AccelerationConfig{
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 5;
        public int HARD_CAP = 5;
        public int BASE_POWER = -9;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public int TIMEOUT = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public AirlineConfig airline = new AirlineConfig();
    public static class AirlineConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 5;
        public int BASE_POWER = 5;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public int BASE_DURATION = 0;
        public int DURATION_PER_RANK = 40;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public AttractiveConfig attractive = new AttractiveConfig();
    public static class AttractiveConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
        public int RANGE = 5;
        public double STRENGTH = .03;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public BufferedConfig buffered = new BufferedConfig();
    public static class BufferedConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -3;
        public int POWER_PER_RANK = 8;
        public int POWER_RANGE = 50;
        public double RECOVERY_RATE = 180;
        public double MAX_PER_RANK = 2;
        public String DISPLAY = "aura";
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public CaveInConfig caveIn = new CaveInConfig();
    public static class CaveInConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 4;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 5;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public int MAX_BLAST_RES = 99;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public DeathWishConfig deathWish = new DeathWishConfig();
    public static class DeathWishConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 4;
        public int HARD_CAP = 4;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 11;
        public int POWER_RANGE = 50;
        public float DAMAGE_FACTOR_BASE = .05f;
        public float DAMAGE_FACTOR_PER_LEVEL = .05f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public DemolitionConfig demolition = new DemolitionConfig();
    public static class DemolitionConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 5;
        public int HARD_CAP = 8;
        public int BASE_POWER = -9;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public float BASE_EXPLOSION_POWER = 1;
        public float EXPLOSION_POWER_PER_RANK = .5F;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public DullnessConfig dullness = new DullnessConfig();
    public static class DullnessConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 5;
        public int HARD_CAP = 10;
        public int BASE_POWER = -7;
        public int POWER_PER_RANK = 8;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public FisherOfMenConfig fisherOfMen = new FisherOfMenConfig();
    public static class FisherOfMenConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 4;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -1;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public float BASE_DAMAGE = 2F;
        public float DAMAGE_PER_LEVEL = 1.5F;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public FleshWoundConfig fleshWound = new FleshWoundConfig();
    public static class FleshWoundConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 4;
        public int HARD_CAP = 4;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 11;
        public int POWER_RANGE = 50;
        public float ABSORPTION_PER_DAMAGE_PER_LEVEL = 0.125F;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public GraceConfig grace = new GraceConfig();
    public static class GraceConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 4;
        public int HARD_CAP = 4;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 11;
        public int POWER_RANGE = 50;
        public int IFRAME_TICKS_PER_LEVEL = 3;
        public float IFRAME_MAGNITUDE_PER_LEVEL = .15F;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public HeartyConfig hearty = new HeartyConfig();
    public static class HeartyConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 4;
        public int HARD_CAP = 4;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 11;
        public int POWER_RANGE = 50;
        public float HEALTH_FACTOR_BASE = .1f;
        public float HEALTH_FACTOR_PER_LEVEL = .1f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public HoverConfig hover = new HoverConfig();
    public static class HoverConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 15;
        public int POWER_RANGE = 50;
        public int DURATION_BASE = 0;
        public int DURATION_PER_LEVEL = 10;
        public float UPWARD_DRIFT = 0.0085f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public ImpersonalConfig impersonal = new ImpersonalConfig();
    public static class ImpersonalConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public JoustingConfig jousting = new JoustingConfig();
    public static class JoustingConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -15;
        public int POWER_PER_RANK = 16;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public LaunchingConfig launching = new LaunchingConfig();
    public static class LaunchingConfig {
    public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 2;
        public int HARD_CAP = 2;
        public int BASE_POWER = -15;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public LegacyConfig legacy = new LegacyConfig();
    public static class LegacyConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 10;
        public int POWER_PER_RANK = 0;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public MetabolisingConfig metabolising = new MetabolisingConfig();
    public static class MetabolisingConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 15;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public float EXHAUSTION_COST = 3.0f;
        public int FOOD_THRESHOLD = 18;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public MonogamousConfig monogamous = new MonogamousConfig();
    public static class MonogamousConfig {
        public boolean ENABLED = false;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 4;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
        public int DURATION = 12000;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public MountedConfig mounted = new MountedConfig();
    public static class MountedConfig {
        public boolean ENABLED = false;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 5;
        public int HARD_CAP = 5;
        public int BASE_POWER = -7;
        public int POWER_PER_RANK = 8;
        public int POWER_RANGE = 50;
        public float DAMAGE_BASE = 0;
        public float DAMAGE_PER_LEVEL = 1.5f;
        public float PROJECTILE_BASE = .5f;
        public float PROJECTILE_PER_LEVEL = .3f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public OutburstConfig outburst = new OutburstConfig();
    public static class OutburstConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
        public int RAGE_PER_HIT = 2;
        public int RAGE_THRESHOLD = 6;
        public float SHOCKWAVE_FORCE = .5f;
        public float SHOCKWAVE_POWER = 6f;
        public float SHOCKWAVE_RANGE = 2f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public PhaseLeapConfig phaseLeap = new PhaseLeapConfig();
    public static class PhaseLeapConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 15;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public PhaseStrafeConfig phaseStrafe = new PhaseStrafeConfig();
    public static class PhaseStrafeConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 15;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public PhotosyntheticConfig photosynthetic = new PhotosyntheticConfig();
    public static class PhotosyntheticConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 15;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public int LIGHT_MINIMUM = 8;
        public int REPAIR_PERIOD = 40;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public PolygamousConfig polygamous = new PolygamousConfig();
    public static class PolygamousConfig {
        public boolean ENABLED = false;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
        public int DURATION = 12000;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public PrimingConfig priming = new PrimingConfig();
    public static class PrimingConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 5;
        public int HARD_CAP = 8;
        public int BASE_POWER = 5;
        public int POWER_PER_RANK = 5;
        public int POWER_RANGE = 20;
        public int DURATION = 60;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public ProspectorConfig prospector = new ProspectorConfig();
    public static class ProspectorConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = 6;
        public int POWER_PER_RANK = 9;
        public int POWER_RANGE = 50;
        public int RADIUS = 3;
        public boolean DETECT_ABUSE = true;
        public int NEW_CHUNK_GRACE_PERIOD = 200;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public RampageConfig rampage = new RampageConfig();
    public static class RampageConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -15;
        public int POWER_PER_RANK = 16;
        public int POWER_RANGE = 50;
        public float DAMAGE_BASE = 5;
        public float DAMAGE_PER_LEVEL = 5;
        public int DURATION_BASE = 20;
        public int DURATION_PER_LEVEL = 10;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public RedAlertConfig redAlert = new RedAlertConfig();
    public static class RedAlertConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 4;
        public int HARD_CAP = 4;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 11;
        public int POWER_RANGE = 50;
        public int RECOVERY_RATE = 240;
        public int RECOVERY_REDUCTION = 40;
        public int SHIELD_DURATION = 600;
        public int MINIMUM_RECOVERY_TIME = 20; //in case enchantment levels get dumb
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public RepulsiveConfig repulsive = new RepulsiveConfig();
    public static class RepulsiveConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
        public int RANGE = 5;
        public double STRENGTH = .03;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public ResurfacingConfig resurfacing = new ResurfacingConfig();
    public static class ResurfacingConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 10;
        public int POWER_PER_RANK = 0;
        public int POWER_RANGE = 50;
        public int ATTEMPTS_PER_CHEST = 6;
        public float CHANCE_PER_ATTEMPT = .35f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public RockCollectorConfig rockCollector = new RockCollectorConfig();
    public static class RockCollectorConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 4;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = -19;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public SaturatedConfig saturated = new SaturatedConfig();
    public static class SaturatedConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -3;
        public int POWER_PER_RANK = 8;
        public int POWER_RANGE = 50;
        public float EXHAUSTION_COST = 1.0f;
        public int FOOD_THRESHOLD = 20;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public ScalpingConfig scalping = new ScalpingConfig();
    public static class ScalpingConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = 6;
        public int POWER_PER_RANK = 9;
        public int POWER_RANGE = 50;
        public float DROP_FACTOR_PER_LEVEL = .3333f;
        public float CARRYOVER_DECAY = .5f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public SelfishConfig selfish = new SelfishConfig();
        public static class SelfishConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 15;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public SkotosyntheticConfig skotosynthetic = new SkotosyntheticConfig();
    public static class SkotosyntheticConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 15;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public int LIGHT_MAXIMUM = 7;
        public int REPAIR_PERIOD = 40;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public SpikesConfig spikes = new SpikesConfig();
    public static class SpikesConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 3;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = -10;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
        public float DAMAGE_PER_LEVEL = .5f;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public StoriedConfig storied = new StoriedConfig();
    public static class StoriedConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = true;
        public boolean IS_FOR_SALE = false;
        public int RARITY = 1;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = 0;
        public int POWER_PER_RANK = 0;
        public int POWER_RANGE = 50;
        public int WORDS_PER_LEVEL_BASE = 20;
        public int WORDS_PER_LEVEL_FIRST_DEGREE = -22;
        public int WORDS_PER_LEVEL_SECOND_DEGREE = 22;
        public boolean ALLOW_NEW_PLOTHOOKS = true;
        public int RARITY_OF_NEW_PLOTHOOKS = 2;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public TetheringConfig tethering = new TetheringConfig();
    public static class TetheringConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 2;
        public int SOFT_CAP = 3;
        public int HARD_CAP = 3;
        public int BASE_POWER = 5;
        public int POWER_PER_RANK = 10;
        public int POWER_RANGE = 50;
        public double ATTRACTION_FACTOR = 1;
        public double LEASH_LENGTH = 5;
    }

    @ConfigEntry.Category("enchantment")
    @ConfigEntry.Gui.CollapsibleObject
    public TrophyCollectorConfig trophyCollector = new TrophyCollectorConfig();
    public static class TrophyCollectorConfig {
        public boolean ENABLED = true;
        public boolean IS_TREASURE = false;
        public boolean IS_FOR_SALE = true;
        public int RARITY = 4;
        public int SOFT_CAP = 1;
        public int HARD_CAP = 1;
        public int BASE_POWER = -19;
        public int POWER_PER_RANK = 20;
        public int POWER_RANGE = 50;
    }
    @ConfigEntry.Category("other")
    public boolean CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS = false;
    @ConfigEntry.Category("other")
    public int POWER_TO_EXCEED_SOFT_CAP = 20;
    @ConfigEntry.Category("other")
    public int COLLECTOR_DISPLAY_UPDATE_PERIOD = 20;
    @ConfigEntry.Category("other")
    public int COLLECTOR_WINDOW_SIZE = 10;

    @ConfigEntry.Category("tomfoolery")
    public boolean DISABLE_INCOMPATIBILITY = false;
    @ConfigEntry.Category("tomfoolery")
    public boolean DESTRUCTIVE_SHOCKWAVES = false;
    @ConfigEntry.Category("tomfoolery")
    public boolean UNLIMITED_CAVE_IN = false;
    @ConfigEntry.Category("tomfoolery")
    public boolean TAKE_ANY_TROPHY = false;
    @ConfigEntry.Category("tomfoolery")
    public boolean COLLECT_ANY_ROCK = false;
    @ConfigEntry.Category("tomfoolery")
    public boolean YANDERE_TOOLS = false;
    @ConfigEntry.Category("tomfoolery")
    public boolean STORIED_WORLD = false;
}
