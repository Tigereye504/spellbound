package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.particle.RedAlertParticle;

public class SBParticles {

    public static final SimpleParticleType RED_ALERT_SHIELD = FabricParticleTypes.simple();

    public static void register(){
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, ResourceLocation.tryBuild(Spellbound.MODID,"red_alert_shield"), RED_ALERT_SHIELD);
    }

    public static void registerClient(){
        ParticleFactoryRegistry.getInstance().register(RED_ALERT_SHIELD, RedAlertParticle.Factory::new);
    }
}
