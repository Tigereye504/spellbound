package net.tigereye.spellbound.mixins;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
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
    @Final
    @Shadow
    private DamageSource damageSource;

    @Shadow
    public static float getSeenPercent(Vec3 vec3, Entity entity){return 0;}

    @Override
    public void collectBlocksAndDamageNonItemEntities() {
        this.level.gameEvent(this.source, GameEvent.EXPLODE, new Vec3(this.x, this.y, this.z));
      Set<BlockPos> set = Sets.newHashSet();

      for(int j = 0; j < 16; ++j) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
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

                     Optional<Float> optional = this.damageCalculator.getBlockExplosionResistance((Explosion)(Object)this, this.level, blockPos, blockState, fluidState);
                     if (optional.isPresent()) {
                        h -= ((Float)optional.get() + 0.3F) * 0.3F;
                     }

                     if (h > 0.0F && this.damageCalculator.shouldBlockExplode((Explosion)(Object)this, this.level, blockPos, blockState, h)) {
                        set.add(blockPos);
                     }

                     m += d * (double)0.3F;
                     n += e * (double)0.3F;
                     o += f * (double)0.3F;
                  }
               }
            }
         }
      }

        this.toBlow.addAll(set);
      float q = this.radius * 2.0F;
      int k = Mth.floor(this.x - (double)q - (double)1.0F);
      int l = Mth.floor(this.x + (double)q + (double)1.0F);
      int r = Mth.floor(this.y - (double)q - (double)1.0F);
      int s = Mth.floor(this.y + (double)q + (double)1.0F);
      int t = Mth.floor(this.z - (double)q - (double)1.0F);
      int u = Mth.floor(this.z + (double)q + (double)1.0F);
      List<Entity> list = this.level.getEntities(this.source, new AABB((double)k, (double)r, (double)t, (double)l, (double)s, (double)u));
      Vec3 vec3 = new Vec3(this.x, this.y, this.z);

        for(Entity entity : list) {
         if (!(entity.ignoreExplosion((Explosion)(Object)this) || entity instanceof ItemEntity)) {
            double v = Math.sqrt(entity.distanceToSqr(vec3)) / (double)q;
            if (v <= (double)1.0F) {
               double w = entity.getX() - this.x;
               double x = (entity instanceof PrimedTnt ? entity.getY() : entity.getEyeY()) - this.y;
               double y = entity.getZ() - this.z;
               double z = Math.sqrt(w * w + x * x + y * y);
               if (z != (double)0.0F) {
                  w /= z;
                  x /= z;
                  y /= z;
                  if (this.damageCalculator.shouldDamageEntity((Explosion)(Object)this, entity)) {
                     entity.hurt(this.damageSource, this.damageCalculator.getEntityDamageAmount((Explosion)(Object)this, entity));
                  }

                  double aa = ((double)1.0F - v) * (double)getSeenPercent(vec3, entity);
                  double ab;
                  if (entity instanceof LivingEntity) {
                     LivingEntity livingEntity = (LivingEntity)entity;
                     ab = ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingEntity, aa);
                  } else {
                     ab = aa;
                  }

                  w *= ab;
                  x *= ab;
                  y *= ab;
                  Vec3 vec32 = new Vec3(w, x, y);
                  entity.setDeltaMovement(entity.getDeltaMovement().add(vec32));
                  if (entity instanceof Player) {
                     Player player = (Player)entity;
                     if (!player.isSpectator() && (!player.isCreative() || !player.getAbilities().flying)) {
                        this.hitPlayers.put(player, vec32);
                     }
                  }
               }
            }
         }
      }
    }
}
