package net.tigereye.spellbound.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.tigereye.spellbound.Spellbound;

@Config(name = Spellbound.MODID)
public class SBConfig implements ConfigData {

    @ConfigEntry.Category("inclusion")
    public boolean ACCELERATION_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int ACCELERATION_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int ACCELERATION_SOFT_CAP = 5;
    @ConfigEntry.Category("level_caps")
    public int ACCELERATION_HARD_CAP = 5;
    @ConfigEntry.Category("enchanting_power")
    public int ACCELERATION_BASE_POWER = -9;
    @ConfigEntry.Category("enchanting_power")
    public int ACCELERATION_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int ACCELERATION_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int ACCELERATION_TIMEOUT = 50;

    @ConfigEntry.Category("inclusion")
    public boolean AIRLINE_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int AIRLINE_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int AIRLINE_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int AIRLINE_HARD_CAP = 5;
    @ConfigEntry.Category("enchanting_power")
    public int AIRLINE_BASE_POWER = 5;
    @ConfigEntry.Category("enchanting_power")
    public int AIRLINE_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int AIRLINE_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int AIRLINE_BASE_DURATION = 0;
    @ConfigEntry.Category("details")
    public int AIRLINE_DURATION_PER_RANK = 40;

    @ConfigEntry.Category("inclusion")
    public boolean ATTRACTIVE_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int ATTRACTIVE_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int ATTRACTIVE_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int ATTRACTIVE_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int ATTRACTIVE_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int ATTRACTIVE_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int ATTRACTIVE_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int ATTRACTION_RANGE = 5;
    @ConfigEntry.Category("details")
    public double ATTRACTION_STRENGTH = .03;

    @ConfigEntry.Category("inclusion")
    public boolean BUFFERED_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int BUFFERED_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int BUFFERED_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int BUFFERED_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int BUFFERED_BASE_POWER = -3;
    @ConfigEntry.Category("enchanting_power")
    public int BUFFERED_POWER_PER_RANK = 8;
    @ConfigEntry.Category("enchanting_power")
    public int BUFFERED_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public double BUFFER_RECOVERY_RATE = 180;
    @ConfigEntry.Category("details")
    public double BUFFER_MAX_PER_RANK = 2;
    @ConfigEntry.Category("details")
    public String BUFFER_DISPLAY = "aura";

    @ConfigEntry.Category("inclusion")
    public boolean CAVE_IN_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int CAVE_IN_RARITY = 4;
    @ConfigEntry.Category("level_caps")
    public int CAVE_IN_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int CAVE_IN_HARD_CAP = 5;
    @ConfigEntry.Category("enchanting_power")
    public int CAVE_IN_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int CAVE_IN_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int CAVE_IN_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int CAVE_IN_MAX_BLAST_RES = 99;

    @ConfigEntry.Category("inclusion")
    public boolean DEATH_WISH_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int DEATH_WISH_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int DEATH_WISH_SOFT_CAP = 4;
    @ConfigEntry.Category("level_caps")
    public int DEATH_WISH_HARD_CAP = 4;
    @ConfigEntry.Category("enchanting_power")
    public int DEATH_WISH_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int DEATH_WISH_POWER_PER_RANK = 11;
    @ConfigEntry.Category("enchanting_power")
    public int DEATH_WISH_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float DEATH_WISH_DAMAGE_FACTOR_BASE = .05f;
    @ConfigEntry.Category("details")
    public float DEATH_WISH_DAMAGE_FACTOR_PER_LEVEL = .05f;

    @ConfigEntry.Category("inclusion")
    public boolean DEMOLITION_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int DEMOLITION_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int DEMOLITION_SOFT_CAP = 5;
    @ConfigEntry.Category("level_caps")
    public int DEMOLITION_HARD_CAP = 8;
    @ConfigEntry.Category("enchanting_power")
    public int DEMOLITION_BASE_POWER = -9;
    @ConfigEntry.Category("enchanting_power")
    public int DEMOLITION_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int DEMOLITION_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float DEMOLITION_BASE_EXPLOSION_POWER = 1;
    @ConfigEntry.Category("details")
    public float DEMOLITION_EXPLOSION_POWER_PER_RANK = .5F;

    @ConfigEntry.Category("inclusion")
    public boolean DULLNESS_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int DULLNESS_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int DULLNESS_SOFT_CAP = 5;
    @ConfigEntry.Category("level_caps")
    public int DULLNESS_HARD_CAP = 5;
    @ConfigEntry.Category("enchanting_power")
    public int DULLNESS_BASE_POWER = -7;
    @ConfigEntry.Category("enchanting_power")
    public int DULLNESS_POWER_PER_RANK = 8;
    @ConfigEntry.Category("enchanting_power")
    public int DULLNESS_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean FISHER_OF_MEN_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int FISHER_OF_MEN_RARITY = 4;
    @ConfigEntry.Category("level_caps")
    public int FISHER_OF_MEN_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int FISHER_OF_MEN_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int FISHER_OF_MEN_BASE_POWER = -1;
    @ConfigEntry.Category("enchanting_power")
    public int FISHER_OF_MEN_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int FISHER_OF_MEN_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float FISHER_OF_MEN_BASE_DAMAGE = 2F;
    @ConfigEntry.Category("details")
    public float FISHER_OF_MEN_DAMAGE_PER_LEVEL = 1.5F;
    @ConfigEntry.Category("details")
    public float FLESH_WOUND_ABSORPTION_PER_DAMAGE_PER_LEVEL = 0.125F;

    @ConfigEntry.Category("inclusion")
    public boolean FLESH_WOUND_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int FLESH_WOUND_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int FLESH_WOUND_SOFT_CAP = 4;
    @ConfigEntry.Category("level_caps")
    public int FLESH_WOUND_HARD_CAP = 4;
    @ConfigEntry.Category("enchanting_power")
    public int FLESH_WOUND_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int FLESH_WOUND_POWER_PER_RANK = 11;
    @ConfigEntry.Category("enchanting_power")
    public int FLESH_WOUND_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean GRACE_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int GRACE_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int GRACE_SOFT_CAP = 4;
    @ConfigEntry.Category("level_caps")
    public int GRACE_HARD_CAP = 4;
    @ConfigEntry.Category("enchanting_power")
    public int GRACE_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int GRACE_POWER_PER_RANK = 11;
    @ConfigEntry.Category("enchanting_power")
    public int GRACE_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int GRACE_IFRAME_TICKS_PER_LEVEL = 3;
    @ConfigEntry.Category("details")
    public float GRACE_IFRAME_MAGNITUDE_PER_LEVEL = .15F;

    @ConfigEntry.Category("inclusion")
    public boolean HEARTY_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int HEARTY_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int HEARTY_SOFT_CAP = 4;
    @ConfigEntry.Category("level_caps")
    public int HEARTY_HARD_CAP = 4;
    @ConfigEntry.Category("enchanting_power")
    public int HEARTY_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int HEARTY_POWER_PER_RANK = 11;
    @ConfigEntry.Category("enchanting_power")
    public int HEARTY_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float HEARTY_HEALTH_FACTOR_BASE = .1f;
    @ConfigEntry.Category("details")
    public float HEARTY_HEALTH_FACTOR_PER_LEVEL = .1f;

    @ConfigEntry.Category("inclusion")
    public boolean HOVER_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int HOVER_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int HOVER_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int HOVER_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int HOVER_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int HOVER_POWER_PER_RANK = 15;
    @ConfigEntry.Category("enchanting_power")
    public int HOVER_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int HOVER_DURATION_BASE = 0;
    @ConfigEntry.Category("details")
    public int HOVER_DURATION_PER_LEVEL = 10;
    
    @ConfigEntry.Category("inclusion")
    public boolean IMPERSONAL_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int IMPERSONAL_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int IMPERSONAL_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int IMPERSONAL_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int IMPERSONAL_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int IMPERSONAL_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int IMPERSONAL_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int INTIMACY_DURATION = 12000;

    @ConfigEntry.Category("inclusion")
    public boolean JOUSTING_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int JOUSTING_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int JOUSTING_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int JOUSTING_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int JOUSTING_BASE_POWER = -15;
    @ConfigEntry.Category("enchanting_power")
    public int JOUSTING_POWER_PER_RANK = 16;
    @ConfigEntry.Category("enchanting_power")
    public int JOUSTING_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean LAUNCHING_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int LAUNCHING_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int LAUNCHING_SOFT_CAP = 2;
    @ConfigEntry.Category("level_caps")
    public int LAUNCHING_HARD_CAP = 2;
    @ConfigEntry.Category("enchanting_power")
    public int LAUNCHING_BASE_POWER = -15;
    @ConfigEntry.Category("enchanting_power")
    public int LAUNCHING_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int LAUNCHING_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean LEGACY_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int LEGACY_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int LEGACY_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int LEGACY_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int LEGACY_BASE_POWER = 10;
    @ConfigEntry.Category("enchanting_power")
    public int LEGACY_POWER_PER_RANK = 0;
    @ConfigEntry.Category("enchanting_power")
    public int LEGACY_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean METABOLISING_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int METABOLISING_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int METABOLISING_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int METABOLISING_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int METABOLISING_BASE_POWER = 15;
    @ConfigEntry.Category("enchanting_power")
    public int METABOLISING_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int METABOLISING_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float METABOLISING_EXHAUSTION_COST = 3.0f;
    @ConfigEntry.Category("details")
    public int METABOLISING_FOOD_THRESHOLD = 18;

    @ConfigEntry.Category("inclusion")
    public boolean MONOGAMOUS_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int MONOGAMOUS_RARITY = 4;
    @ConfigEntry.Category("level_caps")
    public int MONOGAMOUS_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int MONOGAMOUS_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int MONOGAMOUS_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int MONOGAMOUS_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int MONOGAMOUS_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean MOUNTED_ENABLED = false;
    @ConfigEntry.Category("rarity")
    public int MOUNTED_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int MOUNTED_SOFT_CAP = 5;
    @ConfigEntry.Category("level_caps")
    public int MOUNTED_HARD_CAP = 5;
    @ConfigEntry.Category("enchanting_power")
    public int MOUNTED_BASE_POWER = -7;
    @ConfigEntry.Category("enchanting_power")
    public int MOUNTED_POWER_PER_RANK = 8;
    @ConfigEntry.Category("enchanting_power")
    public int MOUNTED_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float MOUNTED_DAMAGE_BASE = 0;
    @ConfigEntry.Category("details")
    public float MOUNTED_DAMAGE_PER_LEVEL = 1.5f;
    @ConfigEntry.Category("details")
    public float MOUNTED_PROJECTILE_BASE = .5f;
    @ConfigEntry.Category("details")
    public float MOUNTED_PROJECTILE_PER_LEVEL = .3f;

    @ConfigEntry.Category("inclusion")
    public boolean OUTBURST_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int OUTBURST_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int OUTBURST_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int OUTBURST_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int OUTBURST_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int OUTBURST_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int OUTBURST_POWER_RANGE = 50;
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

    @ConfigEntry.Category("inclusion")
    public boolean PHASE_LEAP_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int PHASE_LEAP_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int PHASE_LEAP_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int PHASE_LEAP_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int PHASE_LEAP_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int PHASE_LEAP_POWER_PER_RANK = 15;
    @ConfigEntry.Category("enchanting_power")
    public int PHASE_LEAP_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean PHASE_STRAFE_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int PHASE_STRAFE_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int PHASE_STRAFE_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int PHASE_STRAFE_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int PHASE_STRAFE_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int PHASE_STRAFE_POWER_PER_RANK = 15;
    @ConfigEntry.Category("enchanting_power")
    public int PHASE_STRAFE_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean PHOTOSYNTHETIC_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int PHOTOSYNTHETIC_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int PHOTOSYNTHETIC_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int PHOTOSYNTHETIC_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int PHOTOSYNTHETIC_BASE_POWER = 15;
    @ConfigEntry.Category("enchanting_power")
    public int PHOTOSYNTHETIC_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int PHOTOSYNTHETIC_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int PHOTOSYNTHETIC_LIGHT_MINIMUM = 8;
    @ConfigEntry.Category("details")
    public int PHOTOSYNTHETIC_REPAIR_PERIOD = 40;

    @ConfigEntry.Category("inclusion")
    public boolean POLYGAMOUS_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int POLYGAMOUS_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int POLYGAMOUS_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int POLYGAMOUS_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int POLYGAMOUS_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int POLYGAMOUS_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int POLYGAMOUS_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int PROSPECTOR_RADIUS = 3;

    @ConfigEntry.Category("inclusion")
    public boolean PRIMING_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int PRIMING_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int PRIMING_SOFT_CAP = 5;
    @ConfigEntry.Category("level_caps")
    public int PRIMING_HARD_CAP = 5;
    @ConfigEntry.Category("enchanting_power")
    public int PRIMING_BASE_POWER = 5;
    @ConfigEntry.Category("enchanting_power")
    public int PRIMING_POWER_PER_RANK = 5;
    @ConfigEntry.Category("enchanting_power")
    public int PRIMING_POWER_RANGE = 20;

    @ConfigEntry.Category("inclusion")
    public boolean PROSPECTOR_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int PROSPECTOR_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int PROSPECTOR_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int PROSPECTOR_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int PROSPECTOR_BASE_POWER = 6;
    @ConfigEntry.Category("enchanting_power")
    public int PROSPECTOR_POWER_PER_RANK = 9;
    @ConfigEntry.Category("enchanting_power")
    public int PROSPECTOR_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean RAMPAGE_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int RAMPAGE_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int RAMPAGE_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int RAMPAGE_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int RAMPAGE_BASE_POWER = -15;
    @ConfigEntry.Category("enchanting_power")
    public int RAMPAGE_POWER_PER_RANK = 16;
    @ConfigEntry.Category("enchanting_power")
    public int RAMPAGE_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float RAMPAGE_DAMAGE_BASE = 5;
    @ConfigEntry.Category("details")
    public float RAMPAGE_DAMAGE_PER_LEVEL = 5;
    @ConfigEntry.Category("details")
    public int RAMPAGE_DURATION_BASE = 20;
    @ConfigEntry.Category("details")
    public int RAMPAGE_DURATION_PER_LEVEL = 10;

    @ConfigEntry.Category("inclusion")
    public boolean RED_ALERT_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int RED_ALERT_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int RED_ALERT_SOFT_CAP = 4;
    @ConfigEntry.Category("level_caps")
    public int RED_ALERT_HARD_CAP = 4;
    @ConfigEntry.Category("enchanting_power")
    public int RED_ALERT_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int RED_ALERT_POWER_PER_RANK = 11;
    @ConfigEntry.Category("enchanting_power")
    public int RED_ALERT_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int RED_ALERT_RECOVERY_RATE = 240;
    @ConfigEntry.Category("details")
    public int RED_ALERT_RECOVERY_REDUCTION = 40;
    @ConfigEntry.Category("details")
    public int RED_ALERT_SHIELD_DURATION = 600;
    @ConfigEntry.Category("details")
    public int RED_ALERT_MINIMUM_RECOVERY_TIME = 20; //in case enchantment levels get dumb

    @ConfigEntry.Category("inclusion")
    public boolean REPULSIVE_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int REPULSIVE_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int REPULSIVE_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int REPULSIVE_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int REPULSIVE_BASE_POWER = 0;
    @ConfigEntry.Category("enchanting_power")
    public int REPULSIVE_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int REPULSIVE_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean ROCK_COLLECTOR_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int ROCK_COLLECTOR_RARITY = 4;
    @ConfigEntry.Category("level_caps")
    public int ROCK_COLLECTOR_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int ROCK_COLLECTOR_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int ROCK_COLLECTOR_BASE_POWER = -19;
    @ConfigEntry.Category("enchanting_power")
    public int ROCK_COLLECTOR_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int ROCK_COLLECTOR_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean SATURATED_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int SATURATED_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int SATURATED_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int SATURATED_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int SATURATED_BASE_POWER = -3;
    @ConfigEntry.Category("enchanting_power")
    public int SATURATED_POWER_PER_RANK = 8;
    @ConfigEntry.Category("enchanting_power")
    public int SATURATED_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float SATURATED_EXHAUSTION_COST = 1.0f;
    @ConfigEntry.Category("details")
    public int SATURATED_FOOD_THRESHOLD = 20;

    @ConfigEntry.Category("inclusion")
    public boolean SELFISH_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int SELFISH_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int SELFISH_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int SELFISH_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int SELFISH_BASE_POWER = 15;
    @ConfigEntry.Category("enchanting_power")
    public int SELFISH_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int SELFISH_POWER_RANGE = 50;

    @ConfigEntry.Category("inclusion")
    public boolean SKOTOSYNTHETIC_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int SKOTOSYNTHETIC_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int SKOTOSYNTHETIC_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int SKOTOSYNTHETIC_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int SKOTOSYNTHETIC_BASE_POWER = 15;
    @ConfigEntry.Category("enchanting_power")
    public int SKOTOSYNTHETIC_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int SKOTOSYNTHETIC_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int SKOTOSYNTHETIC_LIGHT_MAXIMUM = 7;
    @ConfigEntry.Category("details")
    public int SKOTOSYNTHETIC_REPAIR_PERIOD = 40;

    @ConfigEntry.Category("inclusion")
    public boolean SPIKES_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int SPIKES_RARITY = 3;
    @ConfigEntry.Category("level_caps")
    public int SPIKES_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int SPIKES_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int SPIKES_BASE_POWER = -10;
    @ConfigEntry.Category("enchanting_power")
    public int SPIKES_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int SPIKES_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public float SPIKES_DAMAGE_PER_LEVEL = .5f;

    @ConfigEntry.Category("inclusion")
    public boolean TETHERING_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int TETHERING_RARITY = 2;
    @ConfigEntry.Category("level_caps")
    public int TETHERING_SOFT_CAP = 3;
    @ConfigEntry.Category("level_caps")
    public int TETHERING_HARD_CAP = 3;
    @ConfigEntry.Category("enchanting_power")
    public int TETHERING_BASE_POWER = 5;
    @ConfigEntry.Category("enchanting_power")
    public int TETHERING_POWER_PER_RANK = 10;
    @ConfigEntry.Category("enchanting_power")
    public int TETHERING_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public double TETHER_ATTRACTION_FACTOR = 1;
    @ConfigEntry.Category("details")
    public double TETHER_LEASH_LENGTH = 5;

    @ConfigEntry.Category("inclusion")
    public boolean TROPHY_COLLECTOR_ENABLED = true;
    @ConfigEntry.Category("rarity")
    public int TROPHY_COLLECTOR_RARITY = 4;
    @ConfigEntry.Category("level_caps")
    public int TROPHY_COLLECTOR_SOFT_CAP = 1;
    @ConfigEntry.Category("level_caps")
    public int TROPHY_COLLECTOR_HARD_CAP = 1;
    @ConfigEntry.Category("enchanting_power")
    public int TROPHY_COLLECTOR_BASE_POWER = -19;
    @ConfigEntry.Category("enchanting_power")
    public int TROPHY_COLLECTOR_POWER_PER_RANK = 20;
    @ConfigEntry.Category("enchanting_power")
    public int TROPHY_COLLECTOR_POWER_RANGE = 50;
    @ConfigEntry.Category("details")
    public int COLLECTOR_DISPLAY_UPDATE_PERIOD = 20;
    @ConfigEntry.Category("details")
    public int COLLECTOR_WINDOW_SIZE = 10;

    @ConfigEntry.Category("enchanting_power")
    public int POWER_TO_EXCEED_SOFT_CAP = 20;

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
