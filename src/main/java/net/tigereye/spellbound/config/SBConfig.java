package net.tigereye.spellbound.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.tigereye.spellbound.Spellbound;

@Config(name = Spellbound.MODID)
public class SBConfig implements ConfigData {
    @ConfigEntry.Category("inclusion")
    public boolean ATTRACTIVE_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean CAVE_IN_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean DULLNESS_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean HEARTY_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean IMPERSONAL_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean JOUSTING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean LAUNCHING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean LEGACY_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean MONOGAMOUS_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean MOUNTED_ENABLED = false;
    @ConfigEntry.Category("inclusion")
    public boolean PHASE_LEAP_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean PHASE_STRAFE_ENABLED = true;
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
    public boolean TETHERING_ENABLED = true;
    @ConfigEntry.Category("inclusion")
    public boolean TROPHY_COLLECTOR_ENABLED = true;

    @ConfigEntry.Category("details")
    public int SHIELD_RECOVERY_RATE = 240;
    @ConfigEntry.Category("details")
    public int SHIELD_RECOVERY_REDUCTION = 40;
    @ConfigEntry.Category("details")
    public int SHIELD_DURATION = 600;
    @ConfigEntry.Category("details")
    public int MINIMUM_SHIELD_RECOVERY_TIME = 20; //in case enchantment levels get dumb
    @ConfigEntry.Category("details")
    public int RAMPAGE_DURATION_BASE = 20;
    @ConfigEntry.Category("details")
    public int RAMPAGE_DURATION_PER_LEVEL = 10;
    @ConfigEntry.Category("details")
    public float RAMPAGE_DAMAGE_BASE = 5;
    @ConfigEntry.Category("details")
    public float RAMPAGE_DAMAGE_PER_LEVEL = 5;
    @ConfigEntry.Category("details")
    public double TETHER_ATTRACTION_FACTOR = 1;
    @ConfigEntry.Category("details")
    public int INTIMACY_DURATION = 12000;
    @ConfigEntry.Category("details")
    public int CAVE_IN_MAX_BLAST_RES = 99;
    @ConfigEntry.Category("details")
    public float MOUNTED_DAMAGE_BASE = 0;
    @ConfigEntry.Category("details")
    public float MOUNTED_DAMAGE_PER_LEVEL = 1.5f;
    @ConfigEntry.Category("details")
    public float MOUNTED_PROJECTILE_BASE = .5f;
    @ConfigEntry.Category("details")
    public float MOUNTED_PROJECTILE_PER_LEVEL = .3f;
    @ConfigEntry.Category("details")
    public int ATTRACTION_RANGE = 5;
    @ConfigEntry.Category("details")
    public double ATTRACTION_STRENGTH = .03;
    @ConfigEntry.Category("details")
    public float HEARTY_HEALTH_FACTOR_BASE = .1f;
    @ConfigEntry.Category("details")
    public float HEARTY_HEALTH_FACTOR_PER_LEVEL = .1f;
}
