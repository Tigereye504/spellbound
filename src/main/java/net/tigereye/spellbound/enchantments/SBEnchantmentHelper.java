package net.tigereye.spellbound.enchantments;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.Iterator;
import java.util.List;

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

        forEachEnchantment((enchantment, level, itemStack) -> {
            if(enchantment instanceof SBEnchantment) {
                ((SBEnchantment) enchantment).onProjectileEntityHit(level, itemStack, persistentProjectileEntity, entity);
            }
        }, persistentProjectileEntity.getOwner().getItemsEquipped());//TODO: track launching ItemStack on projectile
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




    @FunctionalInterface
    interface Consumer {
        void accept(Enchantment enchantment, int level, ItemStack itemStack);
    }
}
