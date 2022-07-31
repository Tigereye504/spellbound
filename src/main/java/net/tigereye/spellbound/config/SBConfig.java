package net.tigereye.spellbound.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.tigereye.spellbound.Spellbound;

@Config(name = Spellbound.MODID)
public class SBConfig implements ConfigData {
    @ConfigEntry.Category("inclusion")
    public boolean ACCELERATION_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean ATTRACTIVE_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean BUFFERED_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean CAVE_IN_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean DULLNESS_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean FISHER_OF_MEN_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean FLESH_WOUND_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean HEARTY_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean SATURATED_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean SPIKES_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean IMPERSONAL_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean JOUSTING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean LAUNCHING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean LEGACY_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean METABOLISING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean MONOGAMOUS_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean MOUNTED_ENABLED = false;
    @ConfigEntry.Category("inclusion")
    public boolean OUTBURST_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean PHASE_LEAP_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean PHASE_STRAFE_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean PHOTOSYNTHETIC_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean POLYGAMOUS_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean PRIMING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean RAMPAGE_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean RED_ALERT_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean REPULSIVE_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean ROCK_COLLECTOR_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean SELFISH_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean SKOTOSYNTHETIC_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean TETHERING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean TROPHY_COLLECTOR_ENABLED = true;

    @ConfigEntry.Category("details")
    public int ACCELERATION_TIMEOUT = 50;
    @ConfigEntry.Category("details")
    public int ATTRACTION_RANGE = 5;
    @ConfigEntry.Category("details")
    public double ATTRACTION_STRENGTH = .03;
    @ConfigEntry.Category("details")
    public double BUFFER_RECOVERY_RATE = 180;
    @ConfigEntry.Category("details")
    public double BUFFER_MAX_PER_RANK = 2;
    @ConfigEntry.Category("details")
    public String BUFFERED_DISPLAY = "bar";
    @ConfigEntry.Category("details")
    public int CAVE_IN_MAX_BLAST_RES = 99;
    @ConfigEntry.Category("details")
    public int COLLECTOR_DISPLAY_UPDATE_PERIOD = 20;
    @ConfigEntry.Category("details")
    public int COLLECTOR_WINDOW_SIZE = 10;
    @ConfigEntry.Category("details")
    public float FISHER_OF_MEN_BASE_DAMAGE = 2F;
    @ConfigEntry.Category("details")
    public float FISHER_OF_MEN_DAMAGE_PER_LEVEL = 1.5F;
    @ConfigEntry.Category("details")
    public float FLESH_WOUND_ABSORPTION_PER_DAMAGE_PER_LEVEL = 0.125F;
    @ConfigEntry.Category("details")
    public float HEARTY_HEALTH_FACTOR_BASE = .1f;
    @ConfigEntry.Category("details")
    public float HEARTY_HEALTH_FACTOR_PER_LEVEL = .1f;
    @ConfigEntry.Category("details")
    public int INTIMACY_DURATION = 12000;
    @ConfigEntry.Category("details")
    public float MOUNTED_DAMAGE_BASE = 0;
    @ConfigEntry.Category("details")
    public float MOUNTED_DAMAGE_PER_LEVEL = 1.5f;
    @ConfigEntry.Category("details")
    public float MOUNTED_PROJECTILE_BASE = .5f;
    @ConfigEntry.Category("details")
    public float MOUNTED_PROJECTILE_PER_LEVEL = .3f;
    @ConfigEntry.Category("details")
    public int OUTBURST_RAGE_PER_HIT = 2;
    @ConfigEntry.Category("details")
    public int OUTBURST_RAGE_THRESHOLD = 6;
    @ConfigEntry.Category("details")
    public float OUTBURST_SHOCKWAVE_FORCE = .5f;
    @ConfigEntry.Category("details")
    public float OUTBURST_SHOCKWAVE_POWER = 6f;
    @ConfigEntry.Category("details")
    public float OUTBURST_SHOCKWAVE_RANGE = 2f;
    @ConfigEntry.Category("details")
    public int PHOTOSYNTHETIC_LIGHT_MINIMUM = 8;
    @ConfigEntry.Category("details")
    public int PHOTOSYNTHETIC_REPAIR_PERIOD = 40;
    @ConfigEntry.Category("details")
    public float RAMPAGE_DAMAGE_BASE = 5;
    @ConfigEntry.Category("details")
    public float RAMPAGE_DAMAGE_PER_LEVEL = 5;
    @ConfigEntry.Category("details")
    public int RAMPAGE_DURATION_BASE = 20;
    @ConfigEntry.Category("details")
    public int RAMPAGE_DURATION_PER_LEVEL = 10;
    @ConfigEntry.Category("details")
    public int RED_ALERT_RECOVERY_RATE = 240;
    @ConfigEntry.Category("details")
    public int RED_ALERT_RECOVERY_REDUCTION = 40;
    @ConfigEntry.Category("details")
    public int RED_ALERT_SHIELD_DURATION = 600;
    @ConfigEntry.Category("details")
    public int RED_ALERT_MINIMUM_RECOVERY_TIME = 20; //in case enchantment levels get dumb
    @ConfigEntry.Category("details")
    public float SATURATED_EXHAUSTION_COST = 1.0f;
    @ConfigEntry.Category("details")
    public int SATURATED_FOOD_THRESHOLD = 20;
    @ConfigEntry.Category("details")
    public float SPIKES_DAMAGE_PER_LEVEL = .5f;
    @ConfigEntry.Category("details")
    public float METABOLISING_EXHAUSTION_COST = 3.0f;
    @ConfigEntry.Category("details")
    public int METABOLISING_FOOD_THRESHOLD = 18;
    @ConfigEntry.Category("details")
    public int SKOTOSYNTHETIC_LIGHT_MAXIMUM = 7;
    @ConfigEntry.Category("details")
    public int SKOTOSYNTHETIC_REPAIR_PERIOD = 40;
    @ConfigEntry.Category("details")
    public double TETHER_ATTRACTION_FACTOR = 1;

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
}
