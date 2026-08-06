package net.tigereye.spellbound.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.*;

public class SBStatusEffects {

    public static Holder<MobEffect> BRAVADOS;
    public static Holder<MobEffect> DYING;
    public static Holder<MobEffect> GREEN_SPARKLES;
    public static Holder<MobEffect> HOVERING;
    public static Holder<MobEffect> PESTILENCE;
    public static Holder<MobEffect> PRIMED;
    public static Holder<MobEffect> SHIELDS_DOWN;
    public static Holder<MobEffect> SHIELDED;
    public static Holder<MobEffect> TETHERED;



    public static void register(){
        BRAVADOS = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "bravados"), new Bravados());
        DYING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "dying"), new DyingEffect());
        GREEN_SPARKLES = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "green_sparkles"), new GreenSparkles());
        HOVERING = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "hovering"), new Hovering());
        PESTILENCE = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "pestilence"), new PestilenceEffect());
        PRIMED = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "primed"), new Primed());
        SHIELDS_DOWN = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "shields_down"), new ShieldsDown());
        SHIELDED = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "shielded"), new Shielded());
        TETHERED = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "tethered"), new Tethered());
    }
}
