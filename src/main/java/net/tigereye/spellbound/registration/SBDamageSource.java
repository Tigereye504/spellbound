package net.tigereye.spellbound.registration;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import org.jetbrains.annotations.Nullable;

public class SBDamageSource extends DamageSource {
    public static final DamageSource INFIDELITY = new SBDamageSource("sbInfidelity").setBypassesArmor();
    public static final DamageSource PESTILENCE = new SBDamageSource("sbPestilence").setBypassesArmor();

    public static DamageSource pestilence(@Nullable Entity attacker) {
        return new EntityDamageSource("sbPestilence", attacker).setBypassesArmor().setUsesMagic();
    }

    public SBDamageSource(String name) {
        super(name);
    }
}