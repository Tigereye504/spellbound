package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;

import java.util.List;

public class OutburstEnchantment extends SBEnchantment {

    private static final String OUTBURST_RAGE_NBT_KEY = "SB_Rage";

    public OutburstEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public int getMinPower(int level) {
        return 10 + 20 * (level - 1);
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {

        if(Spellbound.config.OUTBURST_ENABLED) return 3;
        else return 0;
    }

    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(defender.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
            return amount;
        }
        if(!source.isProjectile() && !(source instanceof EntityDamageSource)){
            return amount;
        }
        NbtCompound nbt = stack.getOrCreateNbt();
        int rage = nbt.getInt(OUTBURST_RAGE_NBT_KEY) + Spellbound.config.OUTBURST_RAGE_PER_HIT;
        if(rage >= Spellbound.config.OUTBURST_RAGE_THRESHOLD){
            nbt.remove(OUTBURST_RAGE_NBT_KEY);
            defender.world.createExplosion(defender,defender.getX(),defender.getY(),defender.getZ(),Spellbound.config.OUTBURST_SHOCKWAVE_POWER+level-1, Explosion.DestructionType.NONE);
        }
        else{
            nbt.putInt(OUTBURST_RAGE_NBT_KEY,rage);
        }
        return amount;
    }

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        NbtCompound nbt = stack.getOrCreateNbt();
        if(nbt.contains(OUTBURST_RAGE_NBT_KEY) && entity.world.getTime() % 20 == 0){
            int rage = nbt.getInt(OUTBURST_RAGE_NBT_KEY);
            if(rage <= 1){
                nbt.remove(OUTBURST_RAGE_NBT_KEY);
            }
            else {
                nbt.putInt(OUTBURST_RAGE_NBT_KEY,rage-1);
            }
        }
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() == Items.BOOK
                || super.isAcceptableItem(stack);
    }


    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.THORNS);
    }
}
