package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundClientPlayerEntity;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.NetworkingUtil;
import net.tigereye.spellbound.util.SpellboundUtil;
import net.tigereye.spellbound.util.VectorUtil;

public class PhaseStrafeEnchantment extends SBEnchantment {

    public PhaseStrafeEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.phaseStrafe.RARITY), EnchantmentCategory.ARMOR_LEGS, new EquipmentSlot[] {EquipmentSlot.LEGS},true);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.phaseStrafe.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.phaseStrafe.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.phaseStrafe.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.phaseStrafe.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.phaseStrafe.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.phaseStrafe.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.phaseStrafe.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.phaseStrafe.IS_FOR_SALE;}

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if the user has landed since phasing, reset
        if(entity instanceof SpellboundClientPlayerEntity player) {
            if (player.spellbound$hasMidairJumped() && (entity.onGround() || entity.onClimbable() || entity.isSwimming() || entity.isInWater())) {
                player.spellbound$setHasMidairJumped(false);
            }
        }
        //track Position
        if(!(entity instanceof Player)) {
            ((SpellboundLivingEntity)entity).spellbound$updatePositionTracker(entity.position());
        }
    }

    @Override
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){

        if(entity.isSwimming()
        || entity.isInWater()
        || stack != entity.getItemBySlot(EquipmentSlot.LEGS)){
            return;
        }
        if(!(entity instanceof SpellboundClientPlayerEntity player)) {
            return;
        }
        if(player.spellbound$hasMidairJumped()){
            return;
        }

        Vec3 position = entity.position();
        Vec3 direction;
        if(entity instanceof Player){
            direction = entity.getDeltaMovement();
        }
        else {
            direction = entity.position().subtract(((SpellboundLivingEntity)entity).spellbound$readPositionTracker());
        }
        direction = direction.multiply(1,0,1);
        Vec3 boundingBoxOffset = VectorUtil.getEntityBoundingBoxOffset(direction,entity.getLocalBoundsForPose(entity.getPose()));
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Bounding box offset: [" + boundingBoxOffset.x() + "," + boundingBoxOffset.y() + "," + boundingBoxOffset.z() + "]");
        }
        position = VectorUtil.findCollisionWithStepAssistOnLine(entity.getCommandSenderWorld(),position.add(boundingBoxOffset),direction,level);
        if(position == null){return;}
        position = position.subtract(boundingBoxOffset);
        position = VectorUtil.backtrackToUsableSpace(entity.level(), entity, entity.getBoundingBox(),position);
        if(position == null){return;}
        if(Spellbound.DEBUG) {
            Spellbound.LOGGER.info("Phase Strafe teleporting from position [" + entity.getX() + "," + entity.getY() + "," + entity.getZ() + "]");
            Spellbound.LOGGER.info("Phase Strafe teleporting to position [" + position.x() + "," + position.y() + "," + position.z() + "]");
        }
        NetworkingUtil.sendTeleportRequestPacket(position);
        player.spellbound$setHasMidairJumped(true);
        entity.playSound(SoundEvents.ENDERMAN_TELEPORT,1.0F, 1.0F);
    }
}
