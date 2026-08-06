package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.util.SpellboundUtil;

public class OutburstEnchantment extends SBEnchantment {

    public static final String OUTBURST_RAGE_KEY = "SB_Rage";

    public OutburstEnchantment() {
        super(definition(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.outburst.RARITY), //enchantment weight
            Spellbound.config.outburst.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.outburst.BASE_POWER,Spellbound.config.outburst.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.outburst.BASE_POWER+Spellbound.config.outburst.POWER_RANGE,Spellbound.config.outburst.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.outburst.RARITY-1), //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.outburst.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.outburst.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.outburst.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.outburst.IS_FOR_SALE;}

    @Override
    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){
        if(defender.getItemBySlot(LivingEntity.getEquipmentSlotForItem(stack)) != stack){
            return amount;
        }
        if(source.getEntity() == null){
            return amount;
        }
        
        int rage = stack.getOrDefault(SBComponents.OUTBURST_RAGE,0) + Spellbound.config.outburst.RAGE_PER_HIT;

        if(!defender.level().isClientSide()) {
            int n = (int) (rage * 0.5);
            ((ServerLevel) defender.level()).sendParticles(ParticleTypes.ANGRY_VILLAGER, defender.getX(), defender.getY(0.5), defender.getZ(), n, 0.1, 0.0, 0.1, 0.2);
        }

        if(rage >= Spellbound.config.outburst.RAGE_THRESHOLD){
            stack.remove(SBComponents.OUTBURST_RAGE);
            Vec3 position = defender.position();
            float strength = Spellbound.config.outburst.SHOCKWAVE_POWER*level;
            float range = Spellbound.config.outburst.SHOCKWAVE_RANGE*level;
            float force = Spellbound.config.outburst.SHOCKWAVE_FORCE*level;
            SpellboundUtil.psudeoExplosion(defender,true,position,strength,range,force);
        }
        else{
            stack.set(SBComponents.OUTBURST_RAGE,rage);
        }
        return amount;
    }

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(stack.has(SBComponents.OUTBURST_RAGE) && entity.level().getGameTime() % 20 == 0){
            int rage = stack.get(SBComponents.OUTBURST_RAGE);
            if(rage <= 1){
                stack.remove(SBComponents.OUTBURST_RAGE);
            }
            else {
                stack.set(SBComponents.OUTBURST_RAGE,rage-1);
            }
        }
    }

}
