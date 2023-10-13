package net.tigereye.spellbound.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundProjectileEntity;
import net.tigereye.spellbound.interfaces.TridentEntityItemAccessor;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.mob_effect.instance.PolygamyInstance;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SBEnchantmentHelper {
    public static final String ON_BREAK_LOCKOUT_KEY = Spellbound.MODID+"OnBreakLockout";

    //called after vanilla's getAttackDamage
    public static int beforeDurabilityLoss(ItemStack stack, ServerPlayerEntity user, int loss){
        if(Spellbound.config.STORIED_WORLD && !stack.hasEnchantments()){
            Map<Enchantment,Integer> enchantments = EnchantmentHelper.get(stack);
            enchantments.put(SBEnchantments.STORIED, 1);
            EnchantmentHelper.set(enchantments,stack);
        }
        MutableInt mutableInt = new MutableInt(loss);
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(stack.getName().getString() + " is taking " + loss + " damage before spellbound");
        }
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableInt.setValue(enchantment.beforeDurabilityLoss(level, stack, user, mutableInt.intValue())), stack);

        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(stack.getName().getString() + " is taking " + mutableInt.intValue() + " damage after spellbound");
        }
        return mutableInt.intValue();
    }

    //called after vanilla's getAttackDamage
    public static float getAttackDamage(LivingEntity attacker, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        ItemStack weapon = attacker.getMainHandStack();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getAttackDamage(level, weapon, attacker, defender)), weapon);
        return mutableFloat.floatValue();
    }

    public static float getLocalDifficultyModifier(World world, PlayerEntity player){
        MutableFloat mutableFloat = new MutableFloat(0);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add((enchantment).getLocalDifficultyModifier(level, world, player, itemStack)), player.getItemsEquipped());
        return mutableFloat.floatValue();
    }
    //called at the head of LivingEntity::onKilledBy
    //change: called just before drops onKilledBy
    public static void onDeath(DamageSource source, LivingEntity victim){
        LivingEntity killer = victim.getPrimeAdversary();
        ItemStack projectileSource = null;
        if(source.isProjectile()){
            if(source.getSource() instanceof TridentEntity){
                projectileSource = ((TridentEntityItemAccessor) source.getSource()).spellbound_getTridentStack();
            }
            else if(source.getSource() instanceof SpellboundProjectileEntity) {
                projectileSource = ((SpellboundProjectileEntity) source.getSource()).getSource();
            }
        }
        if(killer != null) {
            if (projectileSource != null) {
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onKill(level, itemStack, source, killer, victim), killer.getArmorItems());
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onKill(level, itemStack, source, killer, victim), projectileSource);
            }
            else{
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onKill(level, itemStack, source, killer, victim), killer.getItemsEquipped());
            }
        }

        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onDeath(level, itemStack, source, killer, victim), victim.getItemsEquipped());
    }

    public static void onEquipmentChange(LivingEntity livingEntity, EquipmentSlot equipmentSlot, ItemStack previousStack, ItemStack currentStack){
        Map<SBEnchantment,Pair<Integer,Integer>> enchantmentsToCheck = new HashMap<>();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) ->
                enchantmentsToCheck.put(enchantment, new Pair<>(level,0)),previousStack);
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantmentsToCheck.containsKey(enchantment)) {
                enchantmentsToCheck.put(enchantment, new Pair<>(enchantmentsToCheck.get(enchantment).getLeft(),level));
            }
            else {
                enchantmentsToCheck.put(enchantment, new Pair<>(0,level));
            }
        },currentStack);
        enchantmentsToCheck.forEach((enchantment,levels) ->
                enchantment.onEquipmentChange(levels.getLeft(),levels.getRight(),previousStack,currentStack,livingEntity));
    }

    public static void onJump(LivingEntity entity){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onJump(level, itemStack, entity), entity.getItemsEquipped());
    }

    public static void onFireProjectile(Entity entity, ItemStack source, ProjectileEntity projectile){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onFireProjectile(level, itemStack, entity, projectile), source);
    }

    public static int onApplyIFrameDuration(int frames, DamageSource source, float damageAmount, LivingEntity defender) {
        MutableInt mutableInt = new MutableInt(frames);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableInt.setValue((enchantment).getIFrameAmount(level, mutableInt.intValue(), source, damageAmount, itemStack, defender)), defender.getArmorItems());
        if(Spellbound.DEBUG && frames != mutableInt.intValue()) {
            Spellbound.LOGGER.info(defender.getName() + "'s Grace Ticks: " + mutableInt.intValue());
        }
        return mutableInt.intValue();
    }

    public static float onApplyIFrameMagnitude(float magnitude, DamageSource source, float damageAmount, LivingEntity defender) {
        MutableFloat mutableFloat = new MutableFloat(magnitude);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue((enchantment).getIFrameMagnitude(level, mutableFloat.floatValue(), source, damageAmount, itemStack, defender)), defender.getArmorItems());
        if(Spellbound.DEBUG && magnitude != mutableFloat.floatValue()) {
            Spellbound.LOGGER.info(defender.getName() + "'s Grace Magnitude: " + mutableFloat.floatValue());
        }
        return mutableFloat.floatValue();
    }

    public static void onMidairJump(SpellboundClientPlayerEntity sbPlayer, PlayerEntity player, boolean isJumping) {
        if (player.isOnGround() || player.isClimbing() || player.isSwimming()) {
            sbPlayer.setJumpReleased(false);
        }
        else if(!isJumping){
            sbPlayer.setJumpReleased(true);
        }
        else if(sbPlayer.getJumpReleased() && !player.getAbilities().flying && !player.hasVehicle()){
            sbPlayer.setJumpReleased(false);
            SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> (enchantment).onMidairJump(level, itemStack, player), player.getItemsEquipped());
        }
    }

    //called at the head of LivingEntity::applyArmor, before armor is actually applied.
    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(enchantment.onPreArmorDefense(level, itemStack, source, defender, mutableFloat.floatValue())), defender.getItemsEquipped());
        return mutableFloat.floatValue();
    }

    //called right after TridentEntity calls getdamage
    public static float getThrownTridentDamage(TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getThrownTridentDamage(level, tridentEntity, itemStack, defender)), tridentItem);
        return mutableFloat.getValue();
    }

    public static void onThrownTridentEntityHit(TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onThrownTridentEntityHit(level, tridentEntity, itemStack, defender), tridentItem);
    }

    public static void onThrowTrident(Entity entity, ItemStack source, TridentEntity projectile){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onThrowTrident(level, itemStack, entity, projectile), source);
    }

    //called at the head of LivingEntity::tick
    public static void onTickWhileEquipped(LivingEntity entity){
        List<SBEnchantment> checked = new LinkedList<>();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment.requiresPreferredSlot()) {
                if (entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(itemStack)) != itemStack) {
                    return;
                }
            }
            if(!checked.contains(enchantment)){
                checked.add(enchantment);
                enchantment.onTickOnceWhileEquipped(level, itemStack, entity);
            }
            enchantment.onTickWhileEquipped(level, itemStack, entity);
        },entity.getItemsEquipped());

    }

    public static void onTickAlways(LivingEntity entity){
        /*for (SBEnchantment enchantment:
                SBEnchantments.SBEnchantmentList) {
            enchantment.onTickAlways(entity);
        }*/ //disabled for now, currently unused
    }

    public static int getArmorAmount(LivingEntity entity) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getArmorAmount(level, itemStack, entity)), entity.getArmorItems());
        return Math.round(mutableFloat.floatValue());
    }

    public static int getProtectionAmount(DamageSource source, LivingEntity target, int k, float amount) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(enchantment.getProtectionAmount(level, source, itemStack, target)), target.getArmorItems());
        return k + Math.round(mutableFloat.floatValue());
    }

    public static float getMiningSpeed(PlayerEntity playerEntity, BlockState block, float h) {
        MutableFloat mutableFloat = new MutableFloat(h);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(enchantment.getMiningSpeed(level, playerEntity, itemStack, block, mutableFloat.getValue())), playerEntity.getMainHandStack());
        return mutableFloat.getValue();
    }

    public static void onActivate(PlayerEntity playerEntity, Entity target, Hand hand){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onActivate(level, playerEntity, itemStack, target), playerEntity.getStackInHand(hand));
    }

    public static void onBreakBlock(Block block, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onBreakBlock(level, itemStack, world, pos, state, player), player.getMainHandStack());
    }

    //public static void onEquipmentChange(LivingEntity entity){
    //    //insert cleanup function here
    //    //entity.
    //    forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onEquipmentChange(level,itemStack,entity),entity.getArmorItems());
    //}


    public static void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        forEachSpellboundEnchantment((((enchantment, level, itemStack) -> enchantment.onInventoryTick(level,stack,world,entity,slot,selected))), stack);
    }

    public static void onRedHealthDamage(DamageSource source, LivingEntity entity, float amount) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onRedHealthDamage(level,itemStack,entity,amount),entity.getItemsEquipped());
    }

    public static void onDoRedHealthDamage(LivingEntity attacker, DamageSource source, LivingEntity victim, float amount) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onDoRedHealthDamage(level,itemStack,attacker,victim,source,amount),attacker.getItemsEquipped());
    }

    public static boolean onItemDestroyed(ItemStack stack, Entity entity) {
        AtomicBoolean willBreak = new AtomicBoolean(true);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> willBreak.set(enchantment.beforeToolBreak(level, itemStack, entity)), stack);
        if(willBreak.get() && !stack.getOrCreateNbt().getBoolean(ON_BREAK_LOCKOUT_KEY)){
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onToolBreak(level, itemStack, entity), stack);
            stack.getOrCreateNbt().putBoolean(ON_BREAK_LOCKOUT_KEY,true);
        }
        return willBreak.get();
    }

    public static void onLegacyToolBreak(ItemStack book, ItemStack stack, Entity entity) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onLegacyToolBreak(level, book, itemStack, entity), stack);
    }

    public static int getProjectileDamage(PersistentProjectileEntity persistentProjectileEntity, EntityHitResult entityHitResult, int damage) {
        Entity entity = persistentProjectileEntity.getOwner();
        MutableFloat mutableFloat = new MutableFloat(damage);
        if(entity != null) {
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(enchantment.getProjectileDamage(level, itemStack, persistentProjectileEntity, entity, entityHitResult.getEntity(), mutableFloat.getValue())), ((SpellboundProjectileEntity)persistentProjectileEntity).getSource());
        }
        return mutableFloat.intValue();
    }

    public static void onProjectileEntityHit(PersistentProjectileEntity persistentProjectileEntity, Entity entity) {
        Entity owner = persistentProjectileEntity.getOwner();
        if(owner != null) {
            if(owner instanceof SpellboundPlayerEntity){
                ((SpellboundPlayerEntity)owner).setIsMakingFullChargeAttack(true);
            }
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onProjectileEntityHit(level, itemStack, persistentProjectileEntity, entity), ((SpellboundProjectileEntity)persistentProjectileEntity).getSource());
        }
    }

    public static void onPullHookedEntity(FishingBobberEntity bobber, ItemStack stack, Entity entity) {
        Entity owner = bobber.getOwner();
        if(owner instanceof LivingEntity) {
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onPullHookedEntity(level, bobber, stack, (LivingEntity)owner, entity), stack);
        }
    }

    public static void onProjectileBlockHit(ProjectileEntity projectileEntity, BlockHitResult blockHitResult) {
        if(projectileEntity instanceof TridentEntity){
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult), ((TridentEntityItemAccessor)projectileEntity).spellbound_getTridentStack());
        }
        else {
            Entity owner = projectileEntity.getOwner();
            if (owner != null) {
                forEachSpellboundEnchantment((enchantment, level, itemStack) -> enchantment.onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult), ((SpellboundProjectileEntity)projectileEntity).getSource());
            }
        }
    }

    public static List<Text> addTooltip(ItemStack stack, List<Text> list, PlayerEntity player, TooltipContext context){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            List<Text> tooltip = enchantment.addTooltip(level, itemStack, player, context);
            if(tooltip != null) {
                list.addAll(tooltip);
            }
        }, stack);
        return list;
    }

    private static void forEachSpellboundEnchantment(SBEnchantmentHelper.Consumer consumer, ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            NbtList NbtList = stack.getEnchantments();

            for(int i = 0; i < NbtList.size(); ++i) {
                String string = NbtList.getCompound(i).getString("id");
                int j = NbtList.getCompound(i).getInt("lvl");
                Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent((enchantment) -> {
                    if(enchantment instanceof SBEnchantment) {
                        consumer.accept((SBEnchantment)enchantment, j, stack);
                    }
                });
            }

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
        return getSpellboundEnchantmentAmountCorrectlyWorn(entity.getItemsEquipped(),target,entity);
    }

    public static int getSpellboundEnchantmentAmountCorrectlyWorn(Iterable<ItemStack> equipment, Enchantment target, LivingEntity entity) {
        MutableInt mutableInt = new MutableInt();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target && entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(itemStack)) == itemStack) {
                mutableInt.add(level);
            }
        }, equipment);
        return mutableInt.intValue();
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
    //returns false if they are pologamous, true if they are monogamous
    public static boolean testOwnerFaithfulness(ItemStack stack, LivingEntity owner){
        if(owner.world.isClient()){
            return true;
        }
        UUID id = loadItemUUID(stack);

        if(owner.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            StatusEffectInstance status = owner.getStatusEffect(SBStatusEffects.POLYGAMY);
            PolygamyInstance polygamy;
            if(!(status instanceof PolygamyInstance)) {
                owner.removeStatusEffect(SBStatusEffects.POLYGAMY);
                polygamy = new PolygamyInstance(id, Spellbound.config.polygamous.DURATION,0,false,false,true);
                owner.addStatusEffect(polygamy);
            }
            else{
                polygamy = (PolygamyInstance) (status);
                owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                if(polygamy.itemUUID == null){
                    owner.removeStatusEffect(SBStatusEffects.POLYGAMY);
                    owner.addStatusEffect(new PolygamyInstance(id, Spellbound.config.polygamous.DURATION, 0, false, false, true));
                    return true;
                }
                if(polygamy.itemUUID.compareTo(id) != 0){
                    polygamy = new PolygamyInstance(id, Spellbound.config.polygamous.DURATION,0,false,false,true);
                    owner.addStatusEffect(polygamy);
                }
            }
            return false;
        }
        else if(owner.hasStatusEffect(SBStatusEffects.MONOGAMY)) {
            StatusEffectInstance status = owner.getStatusEffect(SBStatusEffects.MONOGAMY);
            MonogamyInstance monogamy;
            if(!(status instanceof MonogamyInstance)) {
                owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                monogamy = new MonogamyInstance(id, Spellbound.config.monogamous.DURATION,0,false,false,true);
                owner.addStatusEffect(monogamy);
                return true;
            }
            else{
                monogamy = (MonogamyInstance)(status);
                if(monogamy.itemUUID == null){
                    owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                    owner.addStatusEffect(new MonogamyInstance(id, Spellbound.config.monogamous.DURATION, 0, false, false, true));
                    return true;
                }
                if(monogamy.itemUUID.compareTo(id) != 0) {
                    owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                    owner.addStatusEffect(new PolygamyInstance(id, Spellbound.config.polygamous.DURATION, 0, false, false, true));
                    return false;
                }
            }
        }
        //owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
        owner.addStatusEffect(new MonogamyInstance(id, Spellbound.config.monogamous.DURATION,0,false,false,true));
        return true;
    }

    public static boolean doesPassPreferenceRequirement(SBEnchantment enchantment, ItemStack itemStack, LivingEntity entity){
        if(enchantment.requiresPreferredSlot()) {
            return entity.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(itemStack)) == itemStack;
        }
        return true;
    }

    public static UUID loadItemUUID(ItemStack stack){
        NbtCompound tag = stack.getOrCreateNbt();
        UUID id;
        if(tag.contains(Spellbound.MODID+"ItemID")){
            id = tag.getUuid(Spellbound.MODID+"ItemID");
        }
        else{
            id = UUID.randomUUID();
            tag.putUuid(Spellbound.MODID+"ItemID",id);
        }
        return id;
    }

    //This checks if the given enchantments are in the same enchantment tags and so are incompatible
    //if either enchantment is not registered, return true to assume they are compatible.
    //if any tag contains both enchantments, return false
    //if a tag contains the first and the tag's parents are incompatible with the second, return false.
    //      this is to improve support for other enchantment mods that haven't added spellbound enchantment tags.
    //if all tags are checked and passed, return true.
    public static boolean areNotInSameCategory(SBEnchantment first, Enchantment second) {
        RegistryEntry<Enchantment> firstEntry = getEnchantmentRegistryKey(first);
        RegistryEntry<Enchantment> secondEntry = getEnchantmentRegistryKey(second);
        if(firstEntry == null || secondEntry == null){
            return true;
        }
        for (TagKey<Enchantment> category : SBTags.ENCHANTMENT_CATEGORIES) {
            if(firstEntry.isIn(category) && secondEntry.isIn(category)){
                return false;
            }
            else if (SBTags.CATEGORY_PARENTS.containsKey(category) &&
                    firstEntry.isIn(category) &&
                    !(secondEntry instanceof SBEnchantment)){
                for (Enchantment parent : SBTags.CATEGORY_PARENTS.get(category)) {
                    if(!(second.canCombine(parent))){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static RegistryEntry<Enchantment> getEnchantmentRegistryKey(Enchantment enchantment){
        RegistryKey<Enchantment> key;
        Optional<RegistryKey<Enchantment>> optional = Registry.ENCHANTMENT.getKey(enchantment);
        if(optional.isPresent()) {key = optional.get();}
        else {return null;}
        Optional<RegistryEntry<Enchantment>> optional2 = Registry.ENCHANTMENT.getEntry(key);
        return optional2.orElse(null);
    }

    @FunctionalInterface
    interface Consumer {
        void accept(SBEnchantment enchantment, int level, ItemStack itemStack);
    }
}
