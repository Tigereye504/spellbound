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
    public boolean DEMOLITION_ENABLED = true;
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
    public boolean PROSPECTOR_ENABLED = true;
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

    @ConfigEntry.Category("rarity")
    public int ACCELERATION_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int ATTRACTIVE_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int BUFFERED_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int CAVE_IN_RARITY = 4;
    @ConfigEntry.Category("rarity")
    public int DEMOLITION_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int DULLNESS_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int FISHER_OF_MEN_RARITY = 4;
    @ConfigEntry.Category("rarity")
    public int FLESH_WOUND_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int HEARTY_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int SATURATED_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int SPIKES_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int IMPERSONAL_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int JOUSTING_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int LAUNCHING_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int LEGACY_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int METABOLISING_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int MONOGAMOUS_RARITY = 4;
    @ConfigEntry.Category("rarity")
    public int MOUNTED_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int OUTBURST_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int PROSPECTOR_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int PHASE_LEAP_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int PHASE_STRAFE_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int PHOTOSYNTHETIC_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int POLYGAMOUS_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int PRIMING_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int RAMPAGE_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int RED_ALERT_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int REPULSIVE_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int ROCK_COLLECTOR_RARITY = 4;
    @ConfigEntry.Category("rarity")
    public int SELFISH_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int SKOTOSYNTHETIC_RARITY = 3;
    @ConfigEntry.Category("rarity")
    public int TETHERING_RARITY = 2;
    @ConfigEntry.Category("rarity")
    public int TROPHY_COLLECTOR_RARITY = 4;
    
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
    public String BUFFER_DISPLAY = "aura";
    @ConfigEntry.Category("details")
    public int CAVE_IN_MAX_BLAST_RES = 99;
    @ConfigEntry.Category("details")
    public int COLLECTOR_DISPLAY_UPDATE_PERIOD = 20;
    @ConfigEntry.Category("details")
    public int COLLECTOR_WINDOW_SIZE = 10;
    @ConfigEntry.Category("details")
    public float DEMOLITION_BASE_POWER = 1;
    @ConfigEntry.Category("details")
    public float DEMOLITION_POWER_PER_RANK = .5F;
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
    public int PROSPECTOR_RADIUS = 3;
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
