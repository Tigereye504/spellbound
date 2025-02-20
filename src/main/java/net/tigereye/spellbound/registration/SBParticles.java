package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.particle.RedAlertParticle;

public class SBParticles {

    public static final DefaultParticleType RED_ALERT_SHIELD = FabricParticleTypes.simple();

    public static void register(){
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(Spellbound.MODID,"red_alert_shield"), RED_ALERT_SHIELD);
    }

    public static void registerClient(){
        ParticleFactoryRegistry.getInstance().register(RED_ALERT_SHIELD, RedAlertParticle.Factory::new);
    }
}
