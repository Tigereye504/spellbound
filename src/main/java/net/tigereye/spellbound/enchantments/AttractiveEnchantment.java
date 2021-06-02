package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.tigereye.spellbound.interfaces.UtilityEnchantment;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.List;

public class AttractiveEnchantment extends SBEnchantment implements UtilityEnchantment {


    public AttractiveEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.CHEST});
    }

    @Override
    public int getMinPower(int level) {
        return 20;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        World world = entity.getEntityWorld();
        if(!world.isClient()){
            Vec3d position = entity.getPos();
            List<Entity> entityList = world.getNonSpectatingEntities(Entity.class,
                    new Box(position.x+SBConfig.ATTRACTION_RANGE,position.y+SBConfig.ATTRACTION_RANGE,position.z+SBConfig.ATTRACTION_RANGE,
                            position.x-SBConfig.ATTRACTION_RANGE,position.y-SBConfig.ATTRACTION_RANGE,position.z-SBConfig.ATTRACTION_RANGE));
            for (Entity target:
                 entityList) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                if(target instanceof LivingEntity){
                    forceVec = forceVec.multiply(SBConfig.ATTRACTION_STRENGTH*Math.max(0,1-((LivingEntity)target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                }
                else{
                    forceVec = forceVec.multiply(SBConfig.ATTRACTION_STRENGTH);
                }
                target.addVelocity(forceVec.x,forceVec.y,forceVec.z);
                target.velocityModified = true;
            }
            List<PlayerEntity> playerList = world.getPlayers(TargetPredicate.DEFAULT,entity,
                    new Box(position.x+SBConfig.ATTRACTION_RANGE,position.y+SBConfig.ATTRACTION_RANGE,position.z+SBConfig.ATTRACTION_RANGE,
                            position.x-SBConfig.ATTRACTION_RANGE,position.y-SBConfig.ATTRACTION_RANGE,position.z-SBConfig.ATTRACTION_RANGE));
            for (Entity target:
                    playerList) {
                Vec3d forceVec = position.subtract(target.getPos()).normalize();
                if(target instanceof LivingEntity){
                    forceVec = forceVec.multiply(SBConfig.ATTRACTION_STRENGTH*Math.max(0,1-((LivingEntity)target).getAttributeValue(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE)));
                }
                else{
                    forceVec = forceVec.multiply(SBConfig.ATTRACTION_STRENGTH);
                }
                target.addVelocity(forceVec.x,forceVec.y,forceVec.z);
                target.velocityModified = true;
            }
        }
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof UtilityEnchantment);
    }
}
