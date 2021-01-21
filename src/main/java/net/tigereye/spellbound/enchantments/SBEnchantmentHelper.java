package net.tigereye.spellbound.enchantments;

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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mixins.TridentEntityMixin;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.mob_effect.instance.PolygamyInstance;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBStatusEffects;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SBEnchantmentHelper {

    //called after vanilla's getAttackDamage
    public static float getAttackDamage(LivingEntity attacker, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        ItemStack weapon = attacker.getMainHandStack();
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                mutableFloat.add(((SBEnchantment)enchantment).getAttackDamage(level, weapon, attacker, defender));
            }
        }, weapon);
        return mutableFloat.floatValue();
    }

    //called at the head of LivingEntity::onKilledBy
    public static void onDeath(LivingEntity killer, LivingEntity victim){
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment)enchantment).onKill(level, itemStack, killer, victim);
            }
        },killer.getItemsEquipped());
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment)enchantment).onDeath(level, itemStack, killer, victim);
            }
        },victim.getItemsEquipped());
    }

    //called at the head of LivingEntity::applyArmor, before armor is actually applied.
    public static float onPreArmorDefense(DamageSource source, LivingEntity defender, Float amount){
        MutableFloat mutableFloat = new MutableFloat(amount);
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                mutableFloat.setValue(((SBEnchantment)enchantment).onPreArmorDefense(level, itemStack, source, defender, mutableFloat.floatValue()));
            }
        }, defender.getItemsEquipped());
        return mutableFloat.floatValue();
    }

    //called right after TridentEntity calls getdamage
    public static float getThrownTridentDamage(TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        MutableFloat mutableFloat = new MutableFloat();
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                mutableFloat.add(((SBEnchantment)enchantment).getThrownTridentDamage(level, tridentEntity, itemStack, defender));
            }
        }, tridentItem);
        return mutableFloat.getValue();
    }

    public static void onThrownTridentEntityHit(TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment)enchantment).onThrownTridentEntityHit(level, tridentEntity, itemStack, defender);
            }
        }, tridentItem);
    }

    //called at the head of LivingEntity::tick
    public static void onTickWhileEquipped(LivingEntity entity){
        SBEnchantmentHelper.forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment)enchantment).onTickWhileEquipped(level, itemStack, entity);
            }
        },entity.getItemsEquipped());
    }


    public static int getProtectionAmount(DamageSource source, LivingEntity target, int k, float amount) {
        MutableFloat mutableFloat = new MutableFloat();
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                mutableFloat.add(((SBEnchantment) enchantment).getProtectionAmount(level, source, itemStack, target));
            }
        }, target.getArmorItems());
        return k + mutableFloat.intValue();
    }

    public static float getMiningSpeed(PlayerEntity playerEntity, BlockState block, float h) {
        MutableFloat mutableFloat = new MutableFloat(h);
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                mutableFloat.setValue(((SBEnchantment) enchantment).getMiningSpeed(level, playerEntity, itemStack, block, mutableFloat.getValue()));
            }
        }, playerEntity.getMainHandStack());
        return mutableFloat.getValue();
    }

    public static void onActivate(PlayerEntity playerEntity, Entity target, Hand hand){
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment) enchantment).onActivate(level, playerEntity, itemStack, target);
            }
        }, playerEntity.getStackInHand(hand));
    }

    public static void onBreakBlock(Block block, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment) enchantment).onBreakBlock(level, itemStack, world, pos, state, player);
            }
        }, player.getMainHandStack());
    }

    public static void onToolBreak(ItemStack stack, PlayerEntity entity) {
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment) enchantment).onToolBreak(level, itemStack, entity);
            }
        }, stack);
    }

    public static int getProjectileDamage(PersistentProjectileEntity persistentProjectileEntity, EntityHitResult entityHitResult, int damage) {
        Entity entity = persistentProjectileEntity.getOwner();
        MutableFloat mutableFloat = new MutableFloat(damage);
        if(entity != null) {
            forEachEnchantment((enchantment, level, itemStack) -> {
                if (enchantment instanceof SBEnchantment) {
                    mutableFloat.setValue(((SBEnchantment) enchantment).getProjectileDamage(level, itemStack, persistentProjectileEntity, entity, entityHitResult.getEntity(), mutableFloat.getValue()));
                }
            }, persistentProjectileEntity.getOwner().getItemsEquipped());//TODO: track launching ItemStack on projectile
        }
        return mutableFloat.intValue();
    }

    public static void onProjectileEntityHit(PersistentProjectileEntity persistentProjectileEntity, Entity entity) {
        Entity owner = persistentProjectileEntity.getOwner();
        if(owner != null) {
            forEachEnchantment((enchantment, level, itemStack) -> {
                if (enchantment instanceof SBEnchantment) {
                    ((SBEnchantment) enchantment).onProjectileEntityHit(level, itemStack, persistentProjectileEntity, entity);
                }
            }, owner.getItemsEquipped());//TODO: track launching ItemStack on projectile
        }
    }

    public static void onProjectileBlockHit(ProjectileEntity projectileEntity, BlockHitResult blockHitResult) {
        if(projectileEntity instanceof TridentEntity){
            forEachEnchantment((enchantment, level, itemStack) -> {
                if (enchantment instanceof SBEnchantment) {
                    ((SBEnchantment) enchantment).onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult);
                }
            }, ((TridentEntityMixin.TridentEntityInvoker)projectileEntity).invokeAsItemStack());
        }
        else {
            Entity owner = projectileEntity.getOwner();
            if (owner != null) {
                forEachEnchantment((enchantment, level, itemStack) -> {
                    if (enchantment instanceof SBEnchantment) {
                        ((SBEnchantment) enchantment).onProjectileBlockHit(level, itemStack, projectileEntity, blockHitResult);
                    }
                }, owner.getItemsEquipped());//TODO: track launching ItemStack on projectile
            }
        }
    }

    public static List<Text> addTooltip(ItemStack stack, List<Text> list, PlayerEntity player, TooltipContext context){
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                list.addAll(((SBEnchantment) enchantment).addTooltip(level, itemStack, player, context));
            }
        }, stack);
        return list;
    }

    private static void forEachEnchantment(SBEnchantmentHelper.Consumer consumer, ItemStack stack) {
        if (!stack.isEmpty()) {
            ListTag listTag = stack.getEnchantments();

            for(int i = 0; i < listTag.size(); ++i) {
                String string = listTag.getCompound(i).getString("id");
                int j = listTag.getCompound(i).getInt("lvl");
                Registry.ENCHANTMENT.getOrEmpty(Identifier.tryParse(string)).ifPresent((enchantment) -> {
                    consumer.accept(enchantment, j, stack);
                });
            }

        }
    }
    private static void forEachEnchantment(SBEnchantmentHelper.Consumer consumer, Iterable<ItemStack> stacks) {
        Iterator<ItemStack> var2 = stacks.iterator();

        while(var2.hasNext()) {
            ItemStack itemStack = var2.next();
            forEachEnchantment(consumer, itemStack);
        }

    }

    public static int getEnchantmentAmount(Iterable<ItemStack> equipment, Enchantment target) {
        MutableInt mutableInt = new MutableInt();
        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment == target) {
                mutableInt.add(level);
            }
        }, equipment);
        return mutableInt.intValue();
    }

    public static int countEnchantmentInstances(Iterable<ItemStack> equipment, Enchantment target) {
        MutableInt mutableInt = new MutableInt();
        forEachEnchantment((enchantment, level, itemStack) -> {
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
                polygamy = new PolygamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true);
                owner.applyStatusEffect(polygamy);
            }
            else{
                polygamy = (PolygamyInstance) (status);
                owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                if(polygamy.itemUUID == null){
                    owner.removeStatusEffect(SBStatusEffects.POLYGAMY);
                    owner.applyStatusEffect(new PolygamyInstance(id, SBConfig.INTIMACY_DURATION, 0, false, false, true));
                    return true;
                }
                if(polygamy.itemUUID.compareTo(id) != 0){
                    polygamy = new PolygamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true);
                    owner.applyStatusEffect(polygamy);
                }
            }
            return false;
        }
        else if(owner.hasStatusEffect(SBStatusEffects.MONOGAMY)) {
            StatusEffectInstance status = owner.getStatusEffect(SBStatusEffects.MONOGAMY);
            MonogamyInstance monogamy;
            if(!(status instanceof MonogamyInstance)) {
                owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                monogamy = new MonogamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true);
                owner.applyStatusEffect(monogamy);
                return true;
            }
            else{
                monogamy = (MonogamyInstance)(status);
                if(monogamy.itemUUID == null){
                    owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                    owner.applyStatusEffect(new MonogamyInstance(id, SBConfig.INTIMACY_DURATION, 0, false, false, true));
                    return true;
                }
                if(monogamy.itemUUID.compareTo(id) != 0) {
                    owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                    owner.applyStatusEffect(new PolygamyInstance(id, SBConfig.INTIMACY_DURATION, 0, false, false, true));
                    return false;
                }
            }
        }
        //owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
        owner.applyStatusEffect(new MonogamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true));
        return true;
    }

    public static UUID loadItemUUID(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
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
