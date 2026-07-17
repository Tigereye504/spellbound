package net.tigereye.spellbound.enchantments.lure;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.Objects;

public class FisherOfMenEnchantment extends SBEnchantment {
    static int PRIORITY = 1;

    public FisherOfMenEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.fisherOfMen.RARITY), EnchantmentCategory.FISHING_ROD, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.fisherOfMen.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.fisherOfMen.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.fisherOfMen.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.fisherOfMen.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.fisherOfMen.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.fisherOfMen.POWER_RANGE;}
    @Override
    public int getPriority(){return PRIORITY;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.fisherOfMen.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.fisherOfMen.IS_FOR_SALE;}
    @Override
    public boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other) && other != SBEnchantments.DULLNESS;
    }
    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayer entity, int loss){
        return Math.min(1,loss); //slight hackjob, but fishing rods only lose more than one durability at a time when hooking entities. So we refuse to let that happen.
    }
    @Override
    public void onPullHookedEntity(int level, FishingHook bobber, ItemStack stack, LivingEntity user, Entity target){
        if(target instanceof LivingEntity) {
            target.hurt(user.damageSources().thrown(bobber, user),
                    Spellbound.config.fisherOfMen.BASE_DAMAGE + (Spellbound.config.fisherOfMen.DAMAGE_PER_LEVEL * level));
            if (!target.isAlive() && !bobber.level().isClientSide()) {
                spawnFishingLoot(bobber, stack, user, target);
            }
        }
    }

    private void spawnFishingLoot(FishingHook bobber, ItemStack stack, LivingEntity user, Entity target){
        //calculate launch angles
        double d = user.getX() - bobber.getX();
        double e = user.getY() - bobber.getY();
        double f = user.getZ() - bobber.getZ();

        double vX = d * 0.1D;
        double vY = e * 0.1D + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08D;
        double vZ = f * 0.1D;

        //grab all the loot that is probably the dead entity's
        List<ItemEntity> items = user.level().getEntitiesOfClass(ItemEntity.class, target.getBoundingBox(), Objects::nonNull);
        for (ItemEntity itemEntity:
             items) {
            itemEntity.absMoveTo(bobber.getX(), bobber.getY(), bobber.getZ());
            itemEntity.setDeltaMovement(vX, vY, vZ);
            itemEntity.hasImpulse = true;
            itemEntity.hurtMarked = true;
        }
        //grab all the xp that is problably the dead entity's
        List<ExperienceOrb> xps = user.level().getEntitiesOfClass(ExperienceOrb.class, target.getBoundingBox(), Objects::nonNull);
        for (ExperienceOrb experienceEntity:
                xps) {
            experienceEntity.absMoveTo(bobber.getX(), bobber.getY(), bobber.getZ());
            experienceEntity.setDeltaMovement(vX, vY, vZ);
            experienceEntity.hasImpulse = true;
            experienceEntity.hurtMarked = true;
        }

        //run the fishing loot table and spit out that result too
        float luck = 0;
        Player playerEntity = null;
        if(user instanceof Player){
            playerEntity = (Player)user;
            luck = playerEntity.getLuck();
        }
        LootParams.Builder LCPSBuilder = new LootParams.Builder((ServerLevel) bobber.level()).withParameter(LootContextParams.ORIGIN, bobber.position()).withParameter(LootContextParams.TOOL, stack).withParameter(LootContextParams.THIS_ENTITY, bobber).withLuck(EnchantmentHelper.getFishingLuckBonus(stack) + luck);
        LootTable lootTable = bobber.level().getServer().getLootData().getLootTable(BuiltInLootTables.FISHING);
        LootParams LCPS = LCPSBuilder.create(LootContextParamSets.FISHING);
        List<ItemStack> list = lootTable.getRandomItems(LCPS);
        if(playerEntity != null) {
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayer) playerEntity, stack, bobber, list);
        }
        for (ItemStack itemStack:
                list) {
            ItemEntity itemEntity = new ItemEntity(bobber.level(), bobber.getX(), bobber.getY(), bobber.getZ(), itemStack);

            itemEntity.setDeltaMovement(vX, vY, vZ);
            bobber.level().addFreshEntity(itemEntity);
            if (itemStack.is(ItemTags.FISHES) && playerEntity != null) {
                playerEntity.awardStat(Stats.FISH_CAUGHT, itemStack.getCount());
            }
        }
    }
}
