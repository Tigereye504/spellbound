package net.tigereye.spellbound.enchantments.utility.leggings;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        super(definition(ItemTags.LEG_ARMOR_ENCHANTABLE,
            Spellbound.config.phaseStrafe.WEIGHT, //enchantment weight
            Spellbound.config.phaseStrafe.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.phaseStrafe.BASE_POWER,Spellbound.config.phaseStrafe.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.phaseStrafe.BASE_POWER+Spellbound.config.phaseStrafe.POWER_RANGE,Spellbound.config.phaseStrafe.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.phaseStrafe.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.LEGS}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.phaseStrafe.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.phaseStrafe.SOFT_CAP;}
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
