package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
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
            Vec3d position = defender.getPos();
            float strength = Spellbound.config.OUTBURST_SHOCKWAVE_POWER*level;
            float range = Spellbound.config.OUTBURST_SHOCKWAVE_RANGE*level;
            float force = Spellbound.config.OUTBURST_SHOCKWAVE_FORCE*level;
            List<LivingEntity> entityList = defender.world.getNonSpectatingEntities(LivingEntity.class,
                    new Box(position.x+ range,position.y+range,position.z+range,
                            position.x-range,position.y-range,position.z-range));
            for (LivingEntity target:
                    entityList) {
                if(target != defender) {
                    Vec3d forceVec = target.getPos().subtract(position);
                    float distance = (float) forceVec.length();
                    if(distance < range) {
                        float proximityRatio = (range-distance) / range;
                        target.damage(DamageSource.explosion(defender), strength * proximityRatio);

                        forceVec = forceVec.multiply(1,0,1).add(0,.1,0).normalize();
                        forceVec = forceVec.multiply(force * proximityRatio * Math.max(0, 1 - target.getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));

                        target.addVelocity(forceVec.x, forceVec.y, forceVec.z);
                        target.velocityModified = true;
                    }
                }
            }

            defender.getEntityWorld().playSound(null, defender.getX(), defender.getY(), defender.getZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (defender.world.random.nextFloat() - defender.world.random.nextFloat()) * 0.2F) * 0.7F);
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
