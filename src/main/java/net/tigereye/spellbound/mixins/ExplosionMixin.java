package net.tigereye.spellbound.mixins;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;
import net.tigereye.spellbound.interfaces.SpellboundExplosion;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.*;

@Mixin(Explosion.class)
public class ExplosionMixin implements SpellboundExplosion {

    @Final
    @Shadow
    private World world;
    @Final
    @Shadow
    private double x;
    @Final
    @Shadow
    private double y;
    @Final
    @Shadow
    private double z;
    @Final
    @Nullable
    @Shadow
    private Entity entity;
    @Final
    @Shadow
    private float power;
    @Final
    @Shadow
    private ExplosionBehavior behavior;
    @Final
    @Shadow
    private ObjectArrayList<BlockPos> affectedBlocks;
    @Final
    @Shadow
    private Map<PlayerEntity, Vec3d> affectedPlayers;

    @Override
    public void collectBlocksAndDamageNonItemEntities() {
        this.world.emitGameEvent(this.entity, GameEvent.EXPLODE, new Vec3d(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();
        boolean i = true;

        int k;
        int l;
        for(int j = 0; j < 16; ++j) {
            for(k = 0; k < 16; ++k) {
                for(l = 0; l < 16; ++l) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        double d = (double)((float)j / 15.0F * 2.0F - 1.0F);
                        double e = (double)((float)k / 15.0F * 2.0F - 1.0F);
                        double f = (double)((float)l / 15.0F * 2.0F - 1.0F);
                        double g = Math.sqrt(d * d + e * e + f * f);
                        d /= g;
                        e /= g;
                        f /= g;
                        float h = this.power * (0.7F + this.world.random.nextFloat() * 0.6F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;

                        for(float var21 = 0.3F; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = new BlockPos(m, n, o);
                            BlockState blockState = this.world.getBlockState(blockPos);
                            FluidState fluidState = this.world.getFluidState(blockPos);
                            if (!this.world.isInBuildLimit(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.behavior.getBlastResistance(((Explosion)(Object)this), this.world, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= ((Float)optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.behavior.canDestroyBlock(((Explosion)(Object)this), this.world, blockPos, blockState, h)) {
                                set.add(blockPos);
                            }

                            m += d * 0.30000001192092896D;
                            n += e * 0.30000001192092896D;
                            o += f * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        this.affectedBlocks.addAll(set);
        float q = this.power * 2.0F;
        k = MathHelper.floor(this.x - (double)q - 1.0D);
        l = MathHelper.floor(this.x + (double)q + 1.0D);
        int r = MathHelper.floor(this.y - (double)q - 1.0D);
        int s = MathHelper.floor(this.y + (double)q + 1.0D);
        int t = MathHelper.floor(this.z - (double)q - 1.0D);
        int u = MathHelper.floor(this.z + (double)q + 1.0D);
        assert this.world != null;
        List<Entity> list = this.world.getOtherEntities(this.entity, new Box((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
        Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

        for(int v = 0; v < list.size(); ++v) {
            Entity entity = (Entity)list.get(v);
            if (!entity.isImmuneToExplosion() && !(entity instanceof ItemEntity)) {
                double w = Math.sqrt(entity.squaredDistanceTo(vec3d)) / (double)q;
                if (w <= 1.0D) {
                    double x = entity.getX() - this.x;
                    double y = (entity instanceof TntEntity ? entity.getY() : entity.getEyeY()) - this.y;
                    double z = entity.getZ() - this.z;
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0D) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = (double)Explosion.getExposure(vec3d, entity);
                        double ac = (1.0D - w) * ab;
                        entity.damage(((Explosion)(Object)this).getDamageSource(), (float)((int)((ac * ac + ac) / 2.0D * 7.0D * (double)q + 1.0D)));
                        double ad = ac;
                        if (entity instanceof LivingEntity) {
                            ad = ProtectionEnchantment.transformExplosionKnockback((LivingEntity)entity, ac);
                        }

                        entity.setVelocity(entity.getVelocity().add(x * ad, y * ad, z * ad));
                        if (entity instanceof PlayerEntity) {
                            PlayerEntity playerEntity = (PlayerEntity)entity;
                            if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                                this.affectedPlayers.put(playerEntity, new Vec3d(x * ac, y * ac, z * ac));
                            }
                        }
                    }
                }
            }
        }
    }
}
