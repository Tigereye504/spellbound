package net.tigereye.spellbound.enchantments.lure;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.*;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;
import org.spongepowered.include.com.google.common.base.Predicates;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FisherOfMenEnchantment extends SBEnchantment {

    public FisherOfMenEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.FISHER_OF_MEN_RARITY), EnchantmentTarget.FISHING_ROD, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.FISHER_OF_MEN_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.FISHER_OF_MEN_POWER_PER_RANK * level) - Spellbound.config.FISHER_OF_MEN_BASE_POWER;
        if(level > Spellbound.config.FISHER_OF_MEN_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.FISHER_OF_MEN_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.FISHER_OF_MEN_HARD_CAP;
        else return 0;
    }

    @Override
    public void onPullHookedEntity(int level, FishingBobberEntity bobber, ItemStack stack, LivingEntity user, Entity target){
        target.damage(DamageSource.thrownProjectile(bobber,user),
                Spellbound.config.FISHER_OF_MEN_BASE_DAMAGE + (Spellbound.config.FISHER_OF_MEN_DAMAGE_PER_LEVEL * level));
        if(!target.isAlive() && !bobber.world.isClient()){
            spawnFishingLoot(bobber, stack, user, target);
        }
    }


    @Override
    public boolean isTreasure() {
        return false;
    }

    private void spawnFishingLoot(FishingBobberEntity bobber, ItemStack stack, LivingEntity user, Entity target){
        //calculate launch angles
        double d = user.getX() - bobber.getX();
        double e = user.getY() - bobber.getY();
        double f = user.getZ() - bobber.getZ();
        double g = 0.1D;

        double vX = d * 0.1D;
        double vY = e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D;
        double vZ = f * 0.1D;

        //grab all the loot that is problably the dead entity's
        List<ItemEntity> items = user.getWorld().getEntitiesByClass(ItemEntity.class, target.getBoundingBox(), Objects::nonNull);
        for (ItemEntity itemEntity:
             items) {
            itemEntity.updatePosition(bobber.getX(), bobber.getY(), bobber.getZ());
            itemEntity.setVelocity(vX, vY, vZ);
            itemEntity.velocityDirty = true;
            itemEntity.velocityModified = true;
        }
        //grab all the xp that is problably the dead entity's
        List<ExperienceOrbEntity> xps = user.getWorld().getEntitiesByClass(ExperienceOrbEntity.class, target.getBoundingBox(), Objects::nonNull);
        for (ExperienceOrbEntity experienceEntity:
                xps) {
            experienceEntity.updatePosition(bobber.getX(), bobber.getY(), bobber.getZ());
            experienceEntity.setVelocity(vX, vY, vZ);
            experienceEntity.velocityDirty = true;
            experienceEntity.velocityModified = true;
        }

        //run the fishing loot table and spit out that result too
        float luck = 0;
        PlayerEntity playerEntity = null;
        if(user instanceof PlayerEntity){
            playerEntity = (PlayerEntity)user;
            luck = playerEntity.getLuck();
        }
        LootContext.Builder builder = (new LootContext.Builder((ServerWorld)bobber.world)).parameter(LootContextParameters.ORIGIN, bobber.getPos()).parameter(LootContextParameters.TOOL, stack).parameter(LootContextParameters.THIS_ENTITY, bobber).random(user.getRandom()).luck(EnchantmentHelper.getLuckOfTheSea(stack) + luck);
        LootTable lootTable = bobber.world.getServer().getLootManager().getTable(LootTables.FISHING_GAMEPLAY);
        List<ItemStack> list = lootTable.generateLoot(builder.build(LootContextTypes.FISHING));
        if(playerEntity != null) {
            Criteria.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity) playerEntity, stack, bobber, list);
        }
        for (ItemStack itemStack:
                list) {
            ItemEntity itemEntity = new ItemEntity(bobber.world, bobber.getX(), bobber.getY(), bobber.getZ(), itemStack);

            itemEntity.setVelocity(vX, vY, vZ);
            bobber.world.spawnEntity(itemEntity);
            if (itemStack.isIn(ItemTags.FISHES) && playerEntity != null) {
                playerEntity.increaseStat(Stats.FISH_CAUGHT, 1);
            }
        }
    }
}
