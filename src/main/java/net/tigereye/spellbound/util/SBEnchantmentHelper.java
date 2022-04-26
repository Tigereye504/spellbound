package net.tigereye.spellbound.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.SpellboundPlayerEntity;
import net.tigereye.spellbound.SpellboundProjectileEntity;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.TridentEntityItemAccessor;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.mob_effect.instance.PolygamyInstance;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SBEnchantmentHelper {

    //called after vanilla's getAttackDamage
    public static int beforeDurabilityLoss(ItemStack stack, ServerPlayerEntity user, int loss){
        MutableInt mutableInt = new MutableInt(loss);
        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(stack.getName().getString() + " is taking " + loss + " damage before spellbound");
        }
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableInt.setValue(((SBEnchantment)enchantment).beforeDurabilityLoss(level, stack, user, mutableInt.intValue())), stack);

        if(Spellbound.DEBUG){
            Spellbound.LOGGER.info(stack.getName().getString() + " is taking " + mutableInt.intValue() + " damage after spellbound");
        }
        return mutableInt.intValue();
    }

    //called after vanilla's getAttackDamage
    public static float getAttackDamage(LivingEntity attacker, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        ItemStack weapon = attacker.getMainHandStack();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(((SBEnchantment)enchantment).getAttackDamage(level, weapon, attacker, defender)), weapon);
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
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onKill(level, itemStack, source, killer, victim), killer.getArmorItems());
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onKill(level, itemStack, source, killer, victim), projectileSource);
            }
            else{
                SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onKill(level, itemStack, source, killer, victim), killer.getItemsEquipped());
            }
        }

        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onDeath(level, itemStack, source, killer, victim), victim.getItemsEquipped());
    }

    public static void onJump(LivingEntity entity){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment)enchantment).onJump(level, itemStack, entity), entity.getItemsEquipped());
    }

    //called at the head of LivingEntity::applyArmor, before armor is actually applied.
    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(((SBEnchantment)enchantment).onPreArmorDefense(level, itemStack, source, defender, mutableFloat.floatValue())), defender.getItemsEquipped());
        return mutableFloat.floatValue();
    }

    //called right after TridentEntity calls getdamage
    public static float getThrownTridentDamage(TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(((SBEnchantment)enchantment).getThrownTridentDamage(level, tridentEntity, itemStack, defender)), tridentItem);
        return mutableFloat.getValue();
    }

    public static void onThrownTridentEntityHit(TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment)enchantment).onThrownTridentEntityHit(level, tridentEntity, itemStack, defender), tridentItem);
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
        for (SBEnchantment enchantment:
                SBEnchantments.SBEnchantmentList) {
            enchantment.onTickAlways(entity);
        }
    }

    public static int getProtectionAmount(DamageSource source, LivingEntity target, int k, float amount) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.add(((SBEnchantment) enchantment).getProtectionAmount(level, source, itemStack, target)), target.getArmorItems());
        return k + mutableFloat.intValue();
    }

    public static float getMiningSpeed(PlayerEntity playerEntity, BlockState block, float h) {
        MutableFloat mutableFloat = new MutableFloat(h);
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(((SBEnchantment) enchantment).getMiningSpeed(level, playerEntity, itemStack, block, mutableFloat.getValue())), playerEntity.getMainHandStack());
        return mutableFloat.getValue();
    }

    public static void onActivate(PlayerEntity playerEntity, Entity target, Hand hand){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onActivate(level, playerEntity, itemStack, target), playerEntity.getStackInHand(hand));
    }

    public static void onBreakBlock(Block block, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onBreakBlock(level, itemStack, world, pos, state, player), player.getMainHandStack());
    }

    //TODO: hook this in to a listener... see if one exists for armor specifically
    public static void onEquipmentChange(LivingEntity entity){
        //insert cleanup function here
        //entity.
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onEquipmentChange(level,itemStack,entity),entity.getArmorItems());
    }

    public static void onRedHealthDamage(DamageSource source, LivingEntity entity, float amount) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onRedHealthDamage(level,itemStack,entity,amount),entity.getItemsEquipped());
    }

    public static void onToolBreak(ItemStack stack, PlayerEntity entity) {
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onToolBreak(level, itemStack, entity), stack);
    }

    public static int getProjectileDamage(PersistentProjectileEntity persistentProjectileEntity, EntityHitResult entityHitResult, int damage) {
        Entity entity = persistentProjectileEntity.getOwner();
        MutableFloat mutableFloat = new MutableFloat(damage);
        if(entity != null) {
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> mutableFloat.setValue(((SBEnchantment) enchantment).getProjectileDamage(level, itemStack, persistentProjectileEntity, entity, entityHitResult.getEntity(), mutableFloat.getValue())), ((SpellboundProjectileEntity)persistentProjectileEntity).getSource());
        }
        return mutableFloat.intValue();
    }

    public static void onProjectileEntityHit(PersistentProjectileEntity persistentProjectileEntity, Entity entity) {
        Entity owner = persistentProjectileEntity.getOwner();
        if(owner != null) {
            if(owner instanceof SpellboundPlayerEntity){
                ((SpellboundPlayerEntity)owner).setIsMakingFullChargeAttack(true);
            }
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onProjectileEntityHit(level, itemStack, persistentProjectileEntity, entity), ((SpellboundProjectileEntity)persistentProjectileEntity).getSource());
        }
    }

    public static void onPullHookedEntity(FishingBobberEntity bobber, ItemStack stack, Entity entity) {
        Entity owner = bobber.getOwner();
        if(owner instanceof LivingEntity) {
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onPullHookedEntity(level, bobber, stack, (LivingEntity)owner, entity), stack);
        }
    }

    public static void onProjectileBlockHit(ProjectileEntity projectileEntity, BlockHitResult blockHitResult) {
        if(projectileEntity instanceof TridentEntity){
            forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult), ((TridentEntityItemAccessor)projectileEntity).spellbound_getTridentStack());
        }
        else {
            Entity owner = projectileEntity.getOwner();
            if (owner != null) {
                forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment) enchantment).onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult), ((SpellboundProjectileEntity)projectileEntity).getSource());
            }
        }
    }

    public static List<Text> addTooltip(ItemStack stack, List<Text> list, PlayerEntity player, TooltipContext context){
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            List<Text> tooltip = ((SBEnchantment) enchantment).addTooltip(level, itemStack, player, context);
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
                    doesPassPreferenceRequirement((SBEnchantment) enchantment,itemStack,entity)) {
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
                polygamy = new PolygamyInstance(id, Spellbound.config.INTIMACY_DURATION,0,false,false,true);
                owner.addStatusEffect(polygamy);
            }
            else{
                polygamy = (PolygamyInstance) (status);
                owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                if(polygamy.itemUUID == null){
                    owner.removeStatusEffect(SBStatusEffects.POLYGAMY);
                    owner.addStatusEffect(new PolygamyInstance(id, Spellbound.config.INTIMACY_DURATION, 0, false, false, true));
                    return true;
                }
                if(polygamy.itemUUID.compareTo(id) != 0){
                    polygamy = new PolygamyInstance(id, Spellbound.config.INTIMACY_DURATION,0,false,false,true);
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
                monogamy = new MonogamyInstance(id, Spellbound.config.INTIMACY_DURATION,0,false,false,true);
                owner.addStatusEffect(monogamy);
                return true;
            }
            else{
                monogamy = (MonogamyInstance)(status);
                if(monogamy.itemUUID == null){
                    owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                    owner.addStatusEffect(new MonogamyInstance(id, Spellbound.config.INTIMACY_DURATION, 0, false, false, true));
                    return true;
                }
                if(monogamy.itemUUID.compareTo(id) != 0) {
                    owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                    owner.addStatusEffect(new PolygamyInstance(id, Spellbound.config.INTIMACY_DURATION, 0, false, false, true));
                    return false;
                }
            }
        }
        //owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
        owner.addStatusEffect(new MonogamyInstance(id, Spellbound.config.INTIMACY_DURATION,0,false,false,true));
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
