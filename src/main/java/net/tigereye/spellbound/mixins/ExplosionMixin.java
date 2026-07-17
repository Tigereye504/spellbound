package net.tigereye.spellbound.mixins;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
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
    private Level level;
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
    private Entity source;
    @Final
    @Shadow
    private float radius;
    @Final
    @Shadow
    private ExplosionDamageCalculator damageCalculator;
    @Final
    @Shadow
    private ObjectArrayList<BlockPos> toBlow;
    @Final
    @Shadow
    private Map<Player, Vec3> hitPlayers;

    @Override
    public void collectBlocksAndDamageNonItemEntities() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
        Set<BlockPos> set = Sets.newHashSet();

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
                        float h = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                        double m = this.x;
                        double n = this.y;
                        double o = this.z;

                        for(; h > 0.0F; h -= 0.22500001F) {
                            BlockPos blockPos = BlockPos.containing(m, n, o);
                            BlockState blockState = this.level.getBlockState(blockPos);
                            FluidState fluidState = this.level.getFluidState(blockPos);
                            if (!this.level.isInWorldBounds(blockPos)) {
                                break;
                            }

                            Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance(((Explosion)(Object)this), this.level, blockPos, blockState, fluidState);
                            if (optional.isPresent()) {
                                h -= ((Float)optional.get() + 0.3F) * 0.3F;
                            }

                            if (h > 0.0F && this.damageCalculator.shouldBlockExplode(((Explosion)(Object)this), this.level, blockPos, blockState, h)) {
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

        this.toBlow.addAll(set);
        float q = this.radius * 2.0F;
        k = Mth.floor(this.x - (double)q - 1.0D);
        l = Mth.floor(this.x + (double)q + 1.0D);
        int r = Mth.floor(this.y - (double)q - 1.0D);
        int s = Mth.floor(this.y + (double)q + 1.0D);
        int t = Mth.floor(this.z - (double)q - 1.0D);
        int u = Mth.floor(this.z + (double)q + 1.0D);
        assert this.level != null;
        List<Entity> list = this.level.getEntities(this.source, new AABB((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
        Vec3 vec3d = new Vec3(this.x, this.y, this.z);

        for(int v = 0; v < list.size(); ++v) {
            Entity entity = (Entity)list.get(v);
            if (!entity.ignoreExplosion() && !(entity instanceof ItemEntity)) {
                double w = Math.sqrt(entity.distanceToSqr(vec3d)) / (double)q;
                if (w <= 1.0D) {
                    double x = entity.getX() - this.x;
                    double y = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
                    double z = entity.getZ() - this.z;
                    double aa = Math.sqrt(x * x + y * y + z * z);
                    if (aa != 0.0D) {
                        x /= aa;
                        y /= aa;
                        z /= aa;
                        double ab = (double)Explosion.getSeenPercent(vec3d, entity);
                        double ac = (1.0D - w) * ab;
                        entity.hurt(((Explosion)(Object)this).getDamageSource(), (float)((int)((ac * ac + ac) / 2.0D * 7.0D * (double)q + 1.0D)));
                        double ad = ac;
                        if (entity instanceof LivingEntity) {
                            ad = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, ac);
                        }

                        entity.setDeltaMovement(entity.getDeltaMovement().add(x * ad, y * ad, z * ad));
                        if (entity instanceof Player) {
                            Player playerEntity = (Player)entity;
                            if (!playerEntity.isSpectator() && (!playerEntity.isCreative() || !playerEntity.getAbilities().flying)) {
                                this.hitPlayers.put(playerEntity, new Vec3(x * ac, y * ac, z * ac));
                            }
                        }
                    }
                }
            }
        }
    }
}
