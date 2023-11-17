package net.tigereye.spellbound.registration;

import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import org.jetbrains.annotations.Nullable;

public class SBDamageSources {
    public static final RegistryKey<DamageType> INFIDELITY = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Spellbound.MODID,"infidelity"));
    public static final RegistryKey<DamageType>  PESTILENCE = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier(Spellbound.MODID,"pestilence"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }

    public static DamageSource of(World world, RegistryKey<DamageType> key, @Nullable Entity attacker) {
        if(attacker != null) {
            return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key), attacker);
        }
        else {
            return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
        }
    }
}