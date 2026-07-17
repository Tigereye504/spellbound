package net.tigereye.spellbound.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.*;

public class SBStatusEffects {

    public static MobEffect BRAVADOS = new Bravados();
    public static MobEffect DYING = new DyingEffect();
    public static MobEffect GREEN_SPARKLES = new GreenSparkles();
    public static MobEffect HOVERING = new Hovering();
    public static MobEffect MONOGAMY = new Monogamy();
    public static MobEffect PESTILENCE = new PestilenceEffect();
    public static MobEffect POLYGAMY = new Polygamy();
    public static MobEffect PRIMED = new Primed();
    public static MobEffect SHIELDS_DOWN = new ShieldsDown();
    public static MobEffect SHIELDED = new Shielded();
    public static MobEffect TETHERED = new Tethered();



    public static void register(){
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "bravados"), BRAVADOS);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "dying"), DYING);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "green_sparkles"), GREEN_SPARKLES);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "hovering"), HOVERING);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "monogamy"), MONOGAMY);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "pestilence"), PESTILENCE);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "polygamy"), POLYGAMY);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "primed"), PRIMED);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "shields_down"), SHIELDS_DOWN);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "shielded"), SHIELDED);
        Registry.register(BuiltInRegistries.MOB_EFFECT, new ResourceLocation(Spellbound.MODID, "tethered"), TETHERED);
    }
}
