package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;
import net.tigereye.spellbound.util.VectorUtil;

public class PhaseLeapEnchantment extends SBEnchantment {

    private static final String HAS_PHASED_KEY = Spellbound.MODID+"HasPhased";

    public PhaseLeapEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.PHASE_LEAP_RARITY), EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.PHASE_LEAP_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.PHASE_LEAP_POWER_PER_RANK * level) - Spellbound.config.PHASE_LEAP_BASE_POWER;
        if(level > Spellbound.config.PHASE_LEAP_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.PHASE_LEAP_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.PHASE_LEAP_HARD_CAP;
        else return 0;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if the user has landed since phasing, reset
        NbtCompound tag = stack.getOrCreateNbt();
        if(tag.contains(HAS_PHASED_KEY) && (entity.isOnGround() || entity.isClimbing() || entity.isSwimming())){
            tag.remove(HAS_PHASED_KEY);
        }
    }

    @Override
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){

        if(entity.isSwimming() ||
        stack != entity.getEquippedStack(EquipmentSlot.LEGS)){
            return;
        }
        NbtCompound tag = stack.getOrCreateNbt();
        if(tag.contains(HAS_PHASED_KEY)){
            return;
        }

        Vec3d position = entity.getPos();
        Vec3d direction = entity.getRotationVector();
        direction = direction.multiply(1,0,1);
        Vec3d boundingBoxOffset = VectorUtil.getEntityBoundingBoxOffset(direction,entity.getBoundingBox(entity.getPose()));
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Bounding box offset: [" + boundingBoxOffset.getX() + "," + boundingBoxOffset.getY() + "," + boundingBoxOffset.getZ() + "]");
        }
        position = VectorUtil.findCollisionWithStepAssistOnLine(entity.getEntityWorld(),position.add(boundingBoxOffset),direction,level);
        if(position == null){return;}
        position = position.subtract(boundingBoxOffset);
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Phase leap teleporting from position [" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "]");
            Spellbound.LOGGER.info("Phase leap teleporting to position [" + position.getX() + "," + position.getY() + "," + position.getZ() + "]");
        }
        entity.updatePosition(position.x, position.y, position.z);
        tag.putBoolean(HAS_PHASED_KEY,true);
        entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT,1.0F, 1.0F);
    }
}
