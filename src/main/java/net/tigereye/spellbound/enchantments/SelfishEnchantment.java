package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBEnchantments;

import java.util.Iterator;
import java.util.Random;

public class SelfishEnchantment extends SBEnchantment{

    public SelfishEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 5;
    }

    @Override
    public int getMaxPower(int level) {
        return 51;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(!entity.world.isClient() && stack.isDamaged()){
            Iterator<ItemStack> i = entity.getItemsEquipped().iterator();
            ItemStack target;
            int targetSlot = (int) (entity.world.getTime() % 7);
            switch(targetSlot){
                case 0:
                    target = entity.getEquippedStack(EquipmentSlot.MAINHAND);
                    break;
                case 1:
                    target = entity.getEquippedStack(EquipmentSlot.OFFHAND);
                    break;
                case 2:
                    target = entity.getEquippedStack(EquipmentSlot.HEAD);
                    break;
                case 3:
                    target = entity.getEquippedStack(EquipmentSlot.CHEST);
                    break;
                case 4:
                    target = entity.getEquippedStack(EquipmentSlot.LEGS);
                    break;
                case 5:
                    target = entity.getEquippedStack(EquipmentSlot.FEET);
                    break;
                default:
                    return;
            }
            if(target.isDamageable()
                        && target.getDamage() < target.getMaxDamage() - 1
                        && !EnchantmentHelper.get(target).containsKey(SBEnchantments.SELFISH)){
                ServerPlayerEntity player = null;
                if(entity instanceof ServerPlayerEntity){player = (ServerPlayerEntity)entity;}
                target.damage(1,entity.getRandom(),player);
                stack.setDamage(stack.getDamage()-1);
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other != Enchantments.UNBREAKING
                && other != Enchantments.MENDING;
    }
}
