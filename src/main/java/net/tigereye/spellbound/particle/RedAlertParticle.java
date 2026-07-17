/*
 * Decompiled with CFR 0.2.1 (FabricMC 53fa44c9).
 */
package net.tigereye.spellbound.particle;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;

@Environment(value=EnvType.CLIENT)
public class RedAlertParticle
extends TextureSheetParticle {
    private final SpriteSet spriteProvider;

    RedAlertParticle(ClientLevel world, double x, double y, double z, double d, SpriteSet spriteProvider) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
        float f;
        this.spriteProvider = spriteProvider;
        this.lifetime = 1;
        this.rCol = f = this.random.nextFloat() * 0.6f + 0.4f;
        this.gCol = f;
        this.bCol = f;
        this.quadSize = 1.2f - (float)d * 0.5f;
        this.setSpriteFromAge(this.spriteProvider);
    }

    @Override
    public int getLightColor(float tint) {
        return 0xF000F0;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.remove();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_LIT;
    }

    @Environment(value=EnvType.CLIENT)
    public static class Factory
    implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteProvider;

        public Factory(SpriteSet spriteProvider) {
            this.spriteProvider = spriteProvider;
        }

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new RedAlertParticle(clientWorld, d, e, f, g, this.spriteProvider);
        }
    }
}

