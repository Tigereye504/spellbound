package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SpellboundUtil;
import net.tigereye.spellbound.util.VectorUtil;

public class PhaseLeapEnchantment extends SBEnchantment {

    public PhaseLeapEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.phaseLeap.RARITY), EnchantmentTarget.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.phaseLeap.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.phaseLeap.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.phaseLeap.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.phaseLeap.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.phaseLeap.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.phaseLeap.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.phaseLeap.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.phaseLeap.IS_FOR_SALE;}

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if the user has landed since phasing, reset
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.hasMidairJumped() && (entity.isOnGround() || entity.isClimbing() || entity.isSwimming() || entity.isTouchingWater())) {
                player.setHasMidairJumped(false);
            }
        }
    }

    @Override
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){
        if(entity.isSwimming()
        || entity.isTouchingWater()
        || stack != entity.getEquippedStack(EquipmentSlot.LEGS)){
            return;
        }
        if(!(entity instanceof SpellboundClientPlayerEntity player)) {
            return;
        }
        if(player.hasMidairJumped()){
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
        position = VectorUtil.backtrackToUsableSpace(entity.getWorld(), entity, entity.getBoundingBox(),position);
        if(position == null){return;}
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Phase leap teleporting from position [" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "]");
            Spellbound.LOGGER.info("Phase leap teleporting to position [" + position.getX() + "," + position.getY() + "," + position.getZ() + "]");
        }
        NetworkingUtil.sendTeleportRequestPacket(position);
        player.setHasMidairJumped(true);
        entity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT,1.0F, 1.0F);
    }
}
