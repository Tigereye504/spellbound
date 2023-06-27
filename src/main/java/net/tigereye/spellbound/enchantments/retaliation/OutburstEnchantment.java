package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class OutburstEnchantment extends SBEnchantment {

    private static final String OUTBURST_RAGE_NBT_KEY = "SB_Rage";

    public OutburstEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.outburst.RARITY), EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.outburst.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.outburst.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.outburst.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.outburst.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.outburst.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.outburst.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.outburst.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.outburst.IS_FOR_SALE;}

    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(defender.getEquippedStack(LivingEntity.getPreferredEquipmentSlot(stack)) != stack){
            return amount;
        }
        if(!source.isProjectile() && !(source instanceof EntityDamageSource)){
            return amount;
        }
        NbtCompound nbt = stack.getOrCreateNbt();
        int rage = nbt.getInt(OUTBURST_RAGE_NBT_KEY) + Spellbound.config.outburst.RAGE_PER_HIT;
        if(rage >= Spellbound.config.outburst.RAGE_THRESHOLD){
            nbt.remove(OUTBURST_RAGE_NBT_KEY);
            Vec3d position = defender.getPos();
            float strength = Spellbound.config.outburst.SHOCKWAVE_POWER*level;
            float range = Spellbound.config.outburst.SHOCKWAVE_RANGE*level;
            float force = Spellbound.config.outburst.SHOCKWAVE_FORCE*level;
            SpellboundUtil.psudeoExplosion(defender,true,position,strength,range,force);
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

}
