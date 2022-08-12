package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;
import net.tigereye.spellbound.util.VectorUtil;

public class PhaseStrafeEnchantment extends SBEnchantment {

    public PhaseStrafeEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.PHASE_STRAFE_RARITY), EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.PHASE_STRAFE_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.PHASE_STRAFE_POWER_PER_RANK * level) - Spellbound.config.PHASE_STRAFE_BASE_POWER;
        if(level > Spellbound.config.PHASE_STRAFE_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.PHASE_STRAFE_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.PHASE_STRAFE_HARD_CAP;
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
        Vec3d direction;
        if(entity instanceof PlayerEntity){
            direction = entity.getVelocity();
        }
        else {
            direction = entity.getPos().subtract(((SpellboundLivingEntity)entity).readPositionTracker());
        }
        direction = direction.multiply(1,0,1);
        position = VectorUtil.findCollisionWithStepAssistOnLine(entity.getEntityWorld(),position,direction,level);
        if(position == null){return;}
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Phase Strafe teleporting from position [" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "]");
            Spellbound.LOGGER.info("Phase Strafe teleporting to position [" + position.getX() + "," + position.getY() + "," + position.getZ() + "]");
        }
        entity.updatePosition(position.x, position.y, position.z);
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        if(!(entity instanceof PlayerEntity)) {
            ((SpellboundLivingEntity)entity).updatePositionTracker(entity.getPos());
        }
    }
}
