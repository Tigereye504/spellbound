package net.tigereye.spellbound.registration;

import net.minecraft.entity.damage.DamageSource;

public class SBDamageSource extends DamageSource {
    public static final DamageSource SB_EXAMPLE_DMG = new SBDamageSource("sbExampleDamage").setBypassesArmor();

    public SBDamageSource(String name) {
        super(name);
    }

    
}