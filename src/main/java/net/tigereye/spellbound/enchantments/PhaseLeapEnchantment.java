package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.UtilityEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;
import net.tigereye.spellbound.util.VectorUtil;

public class PhaseLeapEnchantment extends SBEnchantment implements UtilityEnchantment {

    public PhaseLeapEnchantment() {
        super(Rarity.RARE, EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
    }

    @Override
    public int getMinPower(int level) {
        return 15*level;
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level)+30;
    }

    @Override
    public int getMaxLevel() {
        if(Spellbound.config.PHASE_LEAP_ENABLED) return 3;
        else return 0;
    }

    @Override
    public void onJump(int level, ItemStack stack, LivingEntity entity){
        if(!entity.world.isClient && entity instanceof PlayerEntity){
            return;
        }
        if(entity.getPose() == EntityPose.CROUCHING){
            return;
        }
        if(stack != entity.getEquippedStack(EquipmentSlot.LEGS)){
            return;
        }
        Vec3d position = entity.getPos().add(0,.5,0);
        Vec3d direction = entity.getRotationVector();
        direction = direction.multiply(1,0,1);
        position = VectorUtil.findCollisionWithStepAssistOnLine(entity.getEntityWorld(),position,direction,level);
        if(position == null){return;}
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Phase leap teleporting from position [" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "]");
            Spellbound.LOGGER.info("Phase leap teleporting to position [" + position.getX() + "," + position.getY() + "," + position.getZ() + "]");
        }
        entity.updatePosition(position.x, position.y, position.z);
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof UtilityEnchantment);
    }
}
