/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.tigereye.spellbound.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

@Environment(value=EnvType.CLIENT)
public class RedAlertParticle
extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;

    RedAlertParticle(ClientWorld world, double x, double y, double z, double d, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float f;
        this.spriteProvider = spriteProvider;
        this.maxAge = 1;
        this.red = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.green = f;
        this.blue = f;
        this.scale = 1.2f - (float)d * 0.5f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public int getBrightness(float tint) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        this.prevPosX = this.x;
        this.prevPosY = this.y;
        this.prevPosZ = this.z;
        this.markDead();
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_LIT;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new RedAlertParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}

