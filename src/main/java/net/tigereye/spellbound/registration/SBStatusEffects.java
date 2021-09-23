package net.tigereye.spellbound.registration;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.*;

public class SBStatusEffects {

    public static StatusEffect BRAVADOS = new Bravados();
    public static StatusEffect GREEN_SPARKLES = new GreenSparkles();
    public static StatusEffect MONOGAMY = new Monogamy();
    public static StatusEffect POLYGAMY = new Polygamy();
    public static StatusEffect PRIMED = new Primed();
    public static StatusEffect SHIELDS_DOWN = new ShieldsDown();
    public static StatusEffect SHIELDED = new Shielded();
    public static StatusEffect TETHERED = new Tethered();



    public static void register(){
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "bravados"), BRAVADOS);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "green_sparkles"), GREEN_SPARKLES);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "monogamy"), MONOGAMY);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "polygamy"), POLYGAMY);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "primed"), PRIMED);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "shields_down"), SHIELDS_DOWN);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "shielded"), SHIELDED);
        Registry.register(Registry.STATUS_EFFECT, new Identifier(Spellbound.MODID, "tethered"), TETHERED);
    }
}
