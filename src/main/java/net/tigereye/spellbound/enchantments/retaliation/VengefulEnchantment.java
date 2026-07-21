package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class VengefulEnchantment extends SBEnchantment {

    private static final String VENGENCE_NBT_KEY = "SB_Vengence";

    public VengefulEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.spikes.RARITY), EnchantmentCategory.ARMOR_CHEST, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.spikes.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.spikes.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.spikes.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.spikes.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.spikes.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.spikes.POWER_RANGE;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.spikes.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.spikes.IS_FOR_SALE;}

    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        //if time from last injury exceeded retention duration, clear the list
        if(entity.level().isClientSide()){
            return;
        }
        if(entity.tickCount - entity.getLastHurtByMobTimestamp() > Spellbound.config.vengeful.TIMEOUT){
            stack.removeTagKey(VENGENCE_NBT_KEY);
        }
    }

    @Override
    public void onTakeRedHealthDamage(int level, ItemStack stack, DamageSource source, LivingEntity entity, float amount) {
        //If damage was from an attacker, save the attacker and accumulate damage taken from that entity
        Entity attacker = source.getEntity();
        if(attacker != null){
            CompoundTag nbt = stack.getOrCreateTagElement(VENGENCE_NBT_KEY);
            String AttackerUUID = attacker.getStringUUID();
            nbt.putFloat(AttackerUUID,nbt.getFloat(AttackerUUID)+amount);
        }
    }

    @Override
    public void onDoRedHealthDamage(int level, ItemStack stack, LivingEntity user, LivingEntity target, DamageSource source, float amount) {
        //if(user.getWorld().isClient()){
        //    return;
        //}
        //check if the target has enough damaged tracked to trigger.
        if(SBEnchantmentHelper.isEquipmentCorrectlyWorn(stack,user)) {
            CompoundTag nbt = stack.getOrCreateTagElement(VENGENCE_NBT_KEY);
            String targetUUID = target.getStringUUID();
            float excessDamage = nbt.getFloat(targetUUID) - getMinimumDamage(level);
            if(excessDamage > 0){
                VengefulAction vAction = new VengefulAction(Spellbound.config.vengeful.DAMAGE_BASE + (excessDamage * Spellbound.config.vengeful.DAMAGE_RATIO)
                        , user, target, Spellbound.config.vengeful.FOLLOWUP_HIT_DELAY);
                vAction.ifVengeanceInQueueSetAsFollowupElseAddToQueue();
                nbt.remove(targetUUID);
            }
        }
        //if so, create a DelayedAction that hits them more.
    }

    public boolean onClientEntityIsGlowing(int level, ItemStack itemStack, LocalPlayer player, Entity entity, Boolean isGlowing) {
        return isVengeanceReady(itemStack,entity) || isGlowing;
    }

    public int overwriteClientEntityTeamColor(int level, ItemStack itemStack, LocalPlayer player, Entity entity, int color) {
        return isVengeanceReady(itemStack,entity) ? Spellbound.config.vengeful.HIGHLIGHT_COLOR : color;
    }

    private boolean isVengeanceReady(ItemStack itemStack, Entity target){
        CompoundTag nbt = itemStack.getOrCreateTagElement(VENGENCE_NBT_KEY);
        String targetUUID = target.getStringUUID();
        float damage = nbt.getFloat(targetUUID);
        return damage > getMinimumDamage(EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.VENGEFUL,itemStack));
    }

    private float getMinimumDamage(int level){
        return Spellbound.config.vengeful.INJURY_MINIMUM_BASE + (level * Spellbound.config.vengeful.INJURY_MINIMUM_PER_LEVEL);
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() == Items.BOOK
                || super.canEnchant(stack);
    }

    public static class VengefulAction extends DelayedAction {

        VengefulAction followUp = null;
        float damage;
        LivingEntity owner;
        Entity target;

        VengefulAction(float damage, LivingEntity owner, Entity target, int delay){
            this.damage = damage;
            this.owner = owner;
            this.target = target;
            this.setTicks(delay);
        }

        @Override
        public void act(){
            if(target != null) {
                target.hurt(SBDamageSources.of(owner.level(), SBDamageSources.VENGEANCE, owner), damage);
                //spawn slashing particle
                if(target.level() instanceof ServerLevel sWorld) {
                    Vec3 pos = target.position();
                    Vec3 directionOfOwner = owner.position().subtract(target.position()).normalize();
                    sWorld.sendParticles(ParticleTypes.SWEEP_ATTACK,pos.x+directionOfOwner.x, target.getY(0.5), pos.z+directionOfOwner.z,0,0,
                            0, 0, 0);
                }
                if(followUp != null && owner instanceof SpellboundLivingEntity sleOwner) {
                    sleOwner.spellbound$addDelayedAction(followUp);
                }
            }
        }

        public void ifVengeanceInQueueSetAsFollowupElseAddToQueue(){
            if(owner instanceof SpellboundLivingEntity sleOwner){
                for(DelayedAction action : sleOwner.spellbound$getDelayedActions()){
                    if(action instanceof VengefulAction vengefulAction){
                        vengefulAction.receiveFollowup(this);
                        return;
                    }
                }
                sleOwner.spellbound$addDelayedAction(this);
            }
        }

        public void receiveFollowup(VengefulAction vAction){
            if(followUp == null){
                followUp = vAction;
            }
            else {
                followUp.receiveFollowup(vAction);
            }
        }
    }
}
