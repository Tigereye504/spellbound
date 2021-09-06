package net.tigereye.spellbound.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
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
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.UUID;

public class SBEnchantmentHelper {

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
        SBEnchantmentHelper.forEachSpellboundEnchantment((enchantment, level, itemStack) -> ((SBEnchantment)enchantment).onTickWhileEquipped(level, itemStack, entity),entity.getItemsEquipped());
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
                        consumer.accept(enchantment, j, stack);
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

    public static int countSpellboundEnchantmentInstances(Iterable<ItemStack> equipment, Enchantment target) {
        MutableInt mutableInt = new MutableInt();
        forEachSpellboundEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target) {
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



    @FunctionalInterface
    interface Consumer {
        void accept(Enchantment enchantment, int level, ItemStack itemStack);
    }
}
