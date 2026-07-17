package net.tigereye.spellbound.registration;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import org.jetbrains.annotations.Nullable;

public class SBDamageSources {
    public static final ResourceKey<DamageType> INFIDELITY = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Spellbound.MODID,"infidelity"));
    public static final ResourceKey<DamageType>  PESTILENCE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Spellbound.MODID,"pestilence"));
    public static final ResourceKey<DamageType> VENGEANCE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Spellbound.MODID,"vengeance"));

    public static DamageSource of(Level world, ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
    }

    public static DamageSource of(Level world, ResourceKey<DamageType> key, @Nullable Entity attacker) {
        if(attacker != null) {
            return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key), attacker);
        }
        else {
            return new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(key));
        }
    }
}