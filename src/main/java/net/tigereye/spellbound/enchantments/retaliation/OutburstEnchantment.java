package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class OutburstEnchantment extends SBEnchantment {

    private static final String OUTBURST_RAGE_NBT_KEY = "SB_Rage";

    public OutburstEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.outburst.RARITY), EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND},true);
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
    public boolean isTreasureOnly() {return Spellbound.config.outburst.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.outburst.IS_FOR_SALE;}

    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(defender.getItemBySlot(LivingEntity.getEquipmentSlotForItem(stack)) != stack){
            return amount;
        }
        if(source.getEntity() == null){
            return amount;
        }
        CompoundTag nbt = stack.getOrCreateTag();
        int rage = nbt.getInt(OUTBURST_RAGE_NBT_KEY) + Spellbound.config.outburst.RAGE_PER_HIT;

        if(!defender.level().isClientSide()) {
            int n = (int) (rage * 0.5);
            ((ServerLevel) defender.level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, defender.getX(), defender.getY(0.5), defender.getZ(), n, 0.1, 0.0, 0.1, 0.2);
        }

        if(rage >= Spellbound.config.outburst.RAGE_THRESHOLD){
            nbt.remove(OUTBURST_RAGE_NBT_KEY);
            Vec3 position = defender.position();
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
        CompoundTag nbt = stack.getOrCreateTag();
        if(nbt.contains(OUTBURST_RAGE_NBT_KEY) && entity.level().getGameTime() % 20 == 0){
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
    public boolean canEnchant(ItemStack stack) {
        return EnchantmentCategory.ARMOR.canEnchant(stack.getItem())
                || stack.getItem() == Items.BOOK
                || super.canEnchant(stack);
    }

}
