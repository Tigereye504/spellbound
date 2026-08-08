package net.tigereye.spellbound.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundProjectileEntity;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBTags;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.NotNull;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class SBEnchantmentHelper {
    //public static final String ON_BREAK_LOCKOUT_KEY = Spellbound.MODID+"OnBreakLockout";

    //called after vanilla's getAttackDamage
    public static int beforeDurabilityLoss(ItemStack stack, ServerPlayer user, int loss){
        if(Spellbound.config.STORIED_WORLD && !stack.isEnchanted()){
            stack.enchant(SBEnchantments.STORIED, 1);
        }
        MutableInt mutableInt = new MutableInt(loss);
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(stack.getHoverName().getString() + " is taking " + loss + " damage before spellbound");
        }
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableInt.setValue(enchantment.beforeDurabilityLoss(level, stack, user, mutableInt.intValue())), stack);

        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(stack.getHoverName().getString() + " is taking " + mutableInt.intValue() + " damage after spellbound");
        }
        return mutableInt.intValue();
    }

    //called after vanilla's getAttackDamage
    public static float getDamageBonus(LivingEntity attacker, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        ItemStack weapon = attacker.getMainHandItem();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getDamageBonus(level, weapon, attacker, defender)), weapon);
        return mutableFloat.floatValue();
    }

    public static float getLocalDifficultyModifier(Level world, Player player){
        MutableFloat mutableFloat = new MutableFloat(0);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add((enchantment).getLocalDifficultyModifier(level, world, player, itemStack)), player.getAllSlots());
        return mutableFloat.floatValue();
    }

    public static int getLooting(LivingEntity entity){
        MutableInt mutableInt = new MutableInt(0);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableInt.add(enchantment.getLootingValue(level, entity, itemStack)), entity.getAllSlots());
        return mutableInt.intValue();
    }

    //called at the head of LivingEntity::onKilledBy
    //change: called just before drops onKilledBy
    public static void onDeath(DamageSource source, LivingEntity victim){
        LivingEntity killer = victim.getKillCredit();
        ItemStack projectileSource = null;
        if(source.is(DamageTypeTags.IS_PROJECTILE)){
            if(source.getDirectEntity() instanceof ThrownTrident thrownTrident){
                projectileSource = thrownTrident.getPickupItemStackOrigin();
            }
            else if(source.getDirectEntity() instanceof SpellboundProjectileEntity) {
                projectileSource = ((SpellboundProjectileEntity) source.getDirectEntity()).getSource();
            }
        }
        if(killer != null) {
            if (projectileSource != null) {
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onKill(level, itemStack, source, killer, victim), killer.getArmorSlots());
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onKill(level, itemStack, source, killer, victim), projectileSource);
            }
            else{
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onKill(level, itemStack, source, killer, victim), killer.getAllSlots());
            }
        }

        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onDeath(level, itemStack, source, killer, victim), victim.getAllSlots());
    }

    public static void onEquipmentChange(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack){
        Map<SBEnchantment,Tuple<Integer,Integer>> enchantmentsToCheck = new HashMap<>();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) ->
                enchantmentsToCheck.put(enchantment, new Tuple<>(level,0)),previousStack);
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantmentsToCheck.containsKey(enchantment)) {
                enchantmentsToCheck.put(enchantment, new Tuple<>(enchantmentsToCheck.get(enchantment).getA(),level));
            }
            else {
                enchantmentsToCheck.put(enchantment, new Tuple<>(0,level));
            }
        },currentStack);
        enchantmentsToCheck.forEach((enchantment,levels) ->
                enchantment.onEquipmentChangeOnce(levels.getA(),levels.getB(),previousStack,currentStack,livingEntity));
    }

    public static void onJump(LivingEntity entity){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onJump(level, itemStack, entity), entity.getAllSlots());
    }

    public static void onFireProjectile(Entity entity, ItemStack source, Projectile projectile){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onFireProjectile(level, itemStack, entity, projectile), source);
    }

    public static int onApplyIFrameDuration(int frames, DamageSource source, float damageAmount, LivingEntity defender) {
        MutableInt mutableInt = new MutableInt(frames);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableInt.setValue((enchantment).getIFrameAmount(level, mutableInt.intValue(), source, damageAmount, itemStack, defender)), defender.getArmorSlots());
        return mutableInt.intValue();
    }

    public static float onApplyIFrameMagnitude(float magnitude, DamageSource source, float damageAmount, LivingEntity defender) {
        MutableFloat mutableFloat = new MutableFloat(magnitude);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue((enchantment).getIFrameMagnitude(level, mutableFloat.floatValue(), source, damageAmount, itemStack, defender)), defender.getArmorSlots());
        return mutableFloat.floatValue();
    }

    public static void onGainExperience(Player player, int amount) {
        for (SBEnchantment enchantment:
                SBEnchantments.SBEnchantmentList) {
            enchantment.onGainExperienceAlways(player,amount);
        }
    }

    public static void onMidairJump(SpellboundClientPlayerEntity sbPlayer, Player player, boolean isJumping) {
        if (player.onGround() || player.onClimbable() || player.isSwimming()) {
            sbPlayer.spellbound$setJumpReleased(false);
        }
        else if(!isJumping){
            sbPlayer.spellbound$setJumpReleased(true);
        }
        else if(sbPlayer.spellbound$getJumpReleased() && !player.getAbilities().flying && !player.isPassenger()){
            sbPlayer.spellbound$setJumpReleased(false);
            SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> (enchantment).onMidairJump(level, itemStack, player), player.getAllSlots());
        }
    }

    //called at the head of LivingEntity::applyArmor, before armor is actually applied.
    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(enchantment.onPreArmorDefense(level, itemStack, source, defender, mutableFloat.floatValue())), defender.getAllSlots());
        return mutableFloat.floatValue();
    }

    public static void onTargetDamaged(LivingEntity user, Entity defender){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onTargetDamaged(level, itemStack, user, defender), user.getAllSlots());
    }

    //called right after TridentEntity calls getdamage
    public static float getThrownTridentDamage(ThrownTrident tridentEntity, ItemStack tridentItem, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getThrownTridentDamage(level, tridentEntity, itemStack, defender)), tridentItem);
        return mutableFloat.getValue();
    }

    public static void onThrownTridentEntityHit(ThrownTrident tridentEntity, ItemStack tridentItem, Entity defender){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onThrownTridentEntityHit(level, tridentEntity, itemStack, defender), tridentItem);
    }

    public static void onThrowTrident(Entity entity, ItemStack source, ThrownTrident projectile){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onThrowTrident(level, itemStack, entity, projectile), source);
    }

    //called at the head of LivingEntity::tick
    public static void onTickWhileEquipped(LivingEntity entity){
        List<SBEnchantment> checked = new LinkedList<>();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment.requiresPreferredSlot()) {
                if (entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(itemStack)) != itemStack) {
                    return;
                }
            }
            if(!checked.contains(enchantment)){
                checked.add(enchantment);
                enchantment.onTickOnceWhileEquipped(level, itemStack, entity);
            }
            enchantment.onTickWhileEquipped(level, itemStack, entity);
        },entity.getAllSlots());

    }

    public static void onTickAlways(LivingEntity entity){
        /*for (SBEnchantment enchantment:
                SBEnchantments.SBEnchantmentList) {
            enchantment.onTickAlways(entity);
        }*/ //disabled for now, currently unused
    }

    public static int getArmorAmount(LivingEntity entity) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getArmorAmount(level, itemStack, entity)), entity.getArmorSlots());
        return Math.round(mutableFloat.floatValue());
    }

    public static int getProtectionAmount(DamageSource source, LivingEntity target, int k, float amount) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getProtectionAmount(level, source, itemStack, target)), target.getArmorSlots());
        return k + Math.round(mutableFloat.floatValue());
    }
    public static float getMiningSpeed(Player playerEntity, BlockState block, float h) {
        MutableFloat mutableFloat = new MutableFloat(h);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(enchantment.getMiningSpeed(level, playerEntity, itemStack, block, mutableFloat.getValue())), playerEntity.getMainHandItem());
        return mutableFloat.getValue();
    }

    public static void onActivate(Player playerEntity, Entity target, InteractionHand hand){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onActivate(level, playerEntity, itemStack, target), playerEntity.getItemInHand(hand));
    }

    public static void onBreakBlockDirectly(Block block, Level world, BlockPos pos, BlockState state, Player player) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onBreakBlockDirectly(level, itemStack, world, pos, state, player), player.getMainHandItem());
    }

    public static void onBreakBlock(Block block, Level world, BlockPos pos, BlockState state, Player player) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onBreakBlock(level, itemStack, world, pos, state, player), player.getMainHandItem());
    }

    //public static void onEquipmentChange(LivingEntity entity){
    //    //insert cleanup function here
    //    //entity.
    //    forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onEquipmentChange(level,itemStack,entity),entity.getArmorItems());
    //}


    public static void onInventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        forEachSpellboundEnchantment((((enchantment, level, itemStack) -> enchantment.onInventoryTick(level,stack,world,entity,slot,selected))), stack);
    }

    public static void onTakeRedHealthDamage(DamageSource source, @NotNull LivingEntity entity, float redHealthDamage) {
        List<SBEnchantment> checked = new LinkedList<>();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment.requiresPreferredSlot()) {
                if (entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(itemStack)) != itemStack) {
                    return;
                }
            }
            if(!checked.contains(enchantment)){
                checked.add(enchantment);
                enchantment.onTakeRedHealthDamageOnce(level,itemStack,source,entity,redHealthDamage);
            }
            enchantment.onTakeRedHealthDamage(level,itemStack,source,entity,redHealthDamage);
        },entity.getAllSlots());
    }

    public static void afterHeal(LivingEntity entity, float healing){
        List<SBEnchantment> checked = new LinkedList<>();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment.requiresPreferredSlot()) {
                if (entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(itemStack)) != itemStack) {
                    return;
                }
            }
            if(!checked.contains(enchantment)){
                checked.add(enchantment);
                enchantment.afterHealOnce(level,itemStack,entity,healing);
            }
            enchantment.afterHeal(level,itemStack,entity,healing);
        },entity.getAllSlots());
    }

    public static void onDoRedHealthDamage(LivingEntity attacker, DamageSource source, LivingEntity victim, float redHealthDamage) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onDoRedHealthDamage(level,itemStack,attacker,victim,source,redHealthDamage),getAttackEquipment(attacker, source));
    }

    public static boolean onItemDestroyed(ItemStack stack, Entity entity) {
        AtomicBoolean willBreak = new AtomicBoolean(true);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> willBreak.set(enchantment.beforeToolBreak(level, itemStack, entity)), stack);
        if(willBreak.get() /*&& !stack.getOrCreateTag().getBoolean(ON_BREAK_LOCKOUT_KEY)*/){
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onToolBreak(level, itemStack, entity), stack);
            /*stack.getOrCreateTag().putBoolean(ON_BREAK_LOCKOUT_KEY,true);*/
        }
        return willBreak.get();
    }

    public static void onItemUse(ItemStack stack, UseOnContext context, InteractionResult result){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onItemUse(level, itemStack, context, result), stack);
    }

    public static void onLegacyToolBreak(ItemStack book, ItemStack stack, Entity entity) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onLegacyToolBreak(level, book, itemStack, entity), stack);
    }

    public static boolean onLethalDamage(DamageSource source, @NotNull LivingEntity entity) {
        List<SBEnchantment> checked = new LinkedList<>();
        AtomicBoolean saved = new AtomicBoolean(false);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment.requiresPreferredSlot()) {
                if (entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(itemStack)) != itemStack) {
                    return;
                }
            }
            if(!saved.get() && !checked.contains(enchantment)){
                checked.add(enchantment);
                saved.set(enchantment.onLethalDamageOnce(level, source, entity));
            }
            if(!saved.get()) {
                saved.set(enchantment.onLethalDamage(level, source, entity));
            }
        },entity.getAllSlots());
        return saved.get();
    }

    public static int getProjectileDamage(AbstractArrow persistentProjectileEntity, EntityHitResult entityHitResult, int damage) {
        Entity entity = persistentProjectileEntity.getOwner();
        MutableFloat mutableFloat = new MutableFloat(damage);
        if(entity != null) {
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(enchantment.getProjectileDamage(level, itemStack, persistentProjectileEntity, entity, entityHitResult.getEntity(), mutableFloat.getValue())), ((SpellboundProjectileEntity)persistentProjectileEntity).getSource());
        }
        return mutableFloat.intValue();
    }

    public static void onProjectileEntityHit(AbstractArrow persistentProjectileEntity, Entity entity) {
        Entity owner = persistentProjectileEntity.getOwner();
        if(owner != null) {
            if(owner instanceof SpellboundPlayerEntity){
                ((SpellboundPlayerEntity)owner).spellbound$setIsMakingFullChargeAttack(true);
            }
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onProjectileEntityHit(level, itemStack, persistentProjectileEntity, entity), ((SpellboundProjectileEntity)persistentProjectileEntity).getSource());
        }
    }

    public static void onPullHookedEntity(FishingHook bobber, ItemStack stack, Entity entity) {
        Entity owner = bobber.getOwner();
        if(owner instanceof LivingEntity) {
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onPullHookedEntity(level, bobber, stack, (LivingEntity)owner, entity), stack);
        }
    }

    public static void onProjectileBlockHit(Projectile projectileEntity, BlockHitResult blockHitResult) {
        if(projectileEntity instanceof ThrownTrident thrownTrident){
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult), thrownTrident.getPickupItemStackOrigin());
        }
        else {
            Entity owner = projectileEntity.getOwner();
            if (owner != null) {
                forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult), ((SpellboundProjectileEntity)projectileEntity).getSource());
            }
        }
    }

    public static void onStartSleeping(LivingEntity entity){
        for (SBEnchantment enchantment:
                SBEnchantments.SBEnchantmentList) {
            enchantment.onStartSleepingAlways(entity);
        }
    }

    public static void onStatusEffectsCleared(LivingEntity livingEntity) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onStatusEffectsCleared(level, itemStack, livingEntity), livingEntity.getAllSlots());
    }

    public static Boolean onClientEntityIsGlowing(LocalPlayer player, Entity entity, Boolean isGlowing) {
        AtomicBoolean glow = new AtomicBoolean(isGlowing);
        forEachSpellboundEnchantment((((enchantment, level, itemStack) -> glow.set(enchantment.onClientEntityIsGlowing(level,itemStack,player,entity,glow.get())))), player.getAllSlots());
        return glow.get();
    }

    public static int overwriteClientEntityTeamColor(LocalPlayer player, Entity entity, int _color) {
        AtomicInteger color = new AtomicInteger(_color);
        forEachSpellboundEnchantment((((enchantment, level, itemStack) -> color.set(enchantment.overwriteClientEntityTeamColor(level,itemStack,player,entity,color.get())))), player.getAllSlots());
        return color.get();
    }

    public static boolean setItemSuitability(ItemStack stack, BlockState state, Boolean suitability){
        AtomicBoolean ab = new AtomicBoolean(suitability);
        forEachSpellboundEnchantment((enchantment, level, itemStack) ->  ab.set(enchantment.setItemSuitability(level, itemStack, state, ab.get())), stack);
        return ab.get();
    }

    public static List<Component> addTooltip(ItemStack stack, List<Component> list, Player player, TooltipFlag context){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            List<Component> tooltip = enchantment.addTooltip(level, itemStack, player, context);
            if(tooltip != null) {
                list.addAll(tooltip);
            }
        }, stack);
        return list;
    }



    private static void forEachSpellboundEnchantment(SBEnchantmentHelper.Consumer consumer, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            ItemEnchantments enchantments = stack.getEnchantments();
            ArrayList<Tuple<SBEnchantment,Integer>> enchantmentsList = new ArrayList<>();
            enchantments.entrySet().forEach((entry) -> {
                if(entry.getKey().value() instanceof SBEnchantment sbEnchantment) {
                        enchantmentsList.add(new Tuple<>(sbEnchantment,entry.getIntValue()));
                }
            });
            enchantmentsList.sort((o1, o2) -> -Integer.compare(o1.getA().getPriority(), o2.getA().getPriority()));
            enchantmentsList.forEach((enchantment) -> consumer.accept(enchantment.getA(), enchantment.getB(), stack));
        }
    }
    private static void forEachSpellboundEnchantment(SBEnchantmentHelper.Consumer consumer, Iterable<ItemStack> stacks) {
        for (ItemStack itemStack : stacks) {
            forEachSpellboundEnchantment(consumer, itemStack);
        }
    }

    public static int getSpellboundEnchantmentAmount(Iterable<ItemStack> equipment, Enchantment target) {
        MutableInt mutableInt = new MutableInt();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target) {
                mutableInt.add(level);
            }
        }, equipment);
        return mutableInt.intValue();
    }

    public static int getSpellboundEnchantmentAmountCorrectlyWorn(Enchantment target, LivingEntity entity) {
        return getSpellboundEnchantmentAmountCorrectlyWorn(entity.getAllSlots(),target,entity);
    }

    public static int getSpellboundEnchantmentAmountCorrectlyWorn(Iterable<ItemStack> equipment, Enchantment target, LivingEntity entity) {
        MutableInt mutableInt = new MutableInt();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target && isEquipmentCorrectlyWorn(itemStack,entity)) {
                mutableInt.add(level);
            }
        }, equipment);
        return mutableInt.intValue();
    }

    public static boolean isEquipmentCorrectlyWorn(ItemStack itemStack, LivingEntity entity){
        return entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(itemStack)) == itemStack;
    }

    public static int countSpellboundEnchantmentInstances(Iterable<ItemStack> equipment, Enchantment target) {
        MutableInt mutableInt = new MutableInt();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target) {
                mutableInt.add(1);
            }
        }, equipment);
        return mutableInt.intValue();
    }
    public static int countSpellboundEnchantmentInstancesCorrectlyWorn(Iterable<ItemStack> equipment, Enchantment target,LivingEntity entity) {
        MutableInt mutableInt = new MutableInt();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target &&
                    doesPassPreferenceRequirement(enchantment,itemStack,entity)) {
                mutableInt.add(1);
            }
        }, equipment);
        return mutableInt.intValue();
    }

    public static boolean doesPassPreferenceRequirement(SBEnchantment enchantment, ItemStack itemStack, LivingEntity entity){
        if(enchantment.requiresPreferredSlot()) {
            return entity.getItemBySlot(LivingEntity.getEquipmentSlotForItem(itemStack)) == itemStack;
        }
        return true;
    }

    public static Iterable<ItemStack> getAttackEquipment(LivingEntity entity, DamageSource source){
        List<ItemStack> equipment = new ArrayList<>();
        entity.getArmorSlots().forEach((itemStack) -> equipment.add(itemStack));
        if(source.getDirectEntity() instanceof SpellboundProjectileEntity spe){
            ItemStack sourceWeapon = spe.getSource();
            if(sourceWeapon != null){
                equipment.add(sourceWeapon);
            }
        }
        else{
            entity.getHandSlots().forEach((itemStack) -> equipment.add(itemStack));
        }
        return equipment;
    }

    //This checks if the given enchantments are in the same enchantment tags and so are incompatible
    //if either enchantment is not registered, return true to assume they are compatible.
    //if any tag contains both enchantments, return false
    //if a tag contains the first and the tag's parents are incompatible with the second, return false.
    //      this is to improve support for other enchantment mods that haven't added spellbound enchantment tags.
    //if all tags are checked and passed, return true.
    public static boolean areNotInSameCategory(SBEnchantment first, Enchantment second) {
        Holder<Enchantment> firstEntry = getEnchantmentRegistryKey(first);
        Holder<Enchantment> secondEntry = getEnchantmentRegistryKey(second);
        if(firstEntry == null || secondEntry == null){
            return true;
        }
        for (TagKey<Enchantment> category : SBTags.ENCHANTMENT_CATEGORIES) {
            if(firstEntry.is(category) && secondEntry.is(category)){
                return false;
            }
            else if (SBTags.CATEGORY_PARENTS.containsKey(category) &&
                    firstEntry.is(category) &&
                    !(secondEntry instanceof SBEnchantment)){
                for (Enchantment parent : SBTags.CATEGORY_PARENTS.get(category)) {
                    if(!(second.isCompatibleWith(parent))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static Holder<Enchantment> getEnchantmentRegistryKey(Enchantment enchantment){
        ResourceKey<Enchantment> key;
        Optional<ResourceKey<Enchantment>> optional = BuiltInRegistries.ENCHANTMENT.getResourceKey(enchantment);
        if(optional.isPresent()) {key = optional.get();}
        else {return null;}
        Optional<Holder.Reference<Enchantment>> optional2 = BuiltInRegistries.ENCHANTMENT.getHolder(key);
        return optional2.orElse(null);
    }

    public static void runIterationOnItem(EnchantmentVisitor consumer, ItemStack itemStack) {
		ItemEnchantments itemEnchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);

		for (Object2IntMap.Entry<Holder<Enchantment>> entry : itemEnchantments.entrySet()) {
			consumer.accept(entry.getKey().value(), entry.getIntValue());
		}
	}

    @FunctionalInterface //duplicate of EnchantmentHelper's Visitor class
	public interface EnchantmentVisitor {
		void accept(Enchantment enchantment, int i);
	}

    @FunctionalInterface
    interface Consumer {
        void accept(SBEnchantment enchantment, int level, ItemStack itemStack);
    }
}
