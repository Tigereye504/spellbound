package net.tigereye.spellbound.enchantments.retaliation;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.components.VengenceComponent;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBDamageSources;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class VengefulEnchantment extends SBEnchantment {

    public static final String VENGENCE_KEY = Spellbound.MODID+":vengence";
    public static final String VENGENCE_DAMAGE_KEY = Spellbound.MODID+":vengence_damage";
    public static final String VENGENCE_TARGET_KEY = Spellbound.MODID+":vengence_target";

    public VengefulEnchantment() {
        super(definition(ItemTags.ARMOR_ENCHANTABLE, ItemTags.CHEST_ARMOR_ENCHANTABLE,
            Spellbound.config.outburst.WEIGHT, //enchantment weight
            Spellbound.config.outburst.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.outburst.BASE_POWER,Spellbound.config.outburst.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.outburst.BASE_POWER+Spellbound.config.outburst.POWER_RANGE,Spellbound.config.outburst.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.outburst.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.spikes.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.spikes.SOFT_CAP;}
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
            stack.remove(SBComponents.VENGEFUL_GRUDGES);
        }
    }

    @Override
    public void onTakeRedHealthDamage(int level, ItemStack stack, DamageSource source, LivingEntity entity, float amount) {
        //If damage was from an attacker, save the attacker and accumulate damage taken from that entity
        Entity attacker = source.getEntity();
        if(attacker != null){
            VengenceComponent vengenceComponent = stack.getOrDefault(SBComponents.VENGEFUL_GRUDGES, VengenceComponent.ofGrudge(attacker.getStringUUID(), amount));
            stack.set(SBComponents.VENGEFUL_GRUDGES, vengenceComponent);
        }
    }

    @Override
    public void onDoRedHealthDamage(int level, ItemStack stack, LivingEntity user, LivingEntity target, DamageSource source, float amount) {
        //if(user.getWorld().isClient()){
        //    return;
        //}
        //check if the target has enough damaged tracked to trigger.
        if(SBEnchantmentHelper.isEquipmentCorrectlyWorn(stack,user) && stack.has(SBComponents.VENGEFUL_GRUDGES)) {
            VengenceComponent vengenceComponent = stack.get(SBComponents.VENGEFUL_GRUDGES);
            String targetUUID = target.getStringUUID();
            float excessDamage = vengenceComponent.getGrudge(targetUUID) - getMinimumDamage(level);
            if(excessDamage > 0){
                VengefulAction vAction = new VengefulAction(Spellbound.config.vengeful.DAMAGE_BASE + (excessDamage * Spellbound.config.vengeful.DAMAGE_RATIO)
                        , user, target, Spellbound.config.vengeful.FOLLOWUP_HIT_DELAY);
                //if so, create a DelayedAction that hits them more.
                vAction.ifVengeanceInQueueSetAsFollowupElseAddToQueue();
                stack.set(SBComponents.VENGEFUL_GRUDGES, vengenceComponent.withRemovedGrudge(targetUUID));
            }
        }
    }

    public boolean onClientEntityIsGlowing(int level, ItemStack itemStack, LocalPlayer player, Entity entity, Boolean isGlowing) {
        return isVengeanceReady(itemStack,entity) || isGlowing;
    }

    public int overwriteClientEntityTeamColor(int level, ItemStack itemStack, LocalPlayer player, Entity entity, int color) {
        return isVengeanceReady(itemStack,entity) ? Spellbound.config.vengeful.HIGHLIGHT_COLOR : color;
    }

    private boolean isVengeanceReady(ItemStack itemStack, Entity target){
        if(itemStack.has(SBComponents.VENGEFUL_GRUDGES)){
            return false;
        }
        VengenceComponent grudges = itemStack.get(SBComponents.VENGEFUL_GRUDGES);
        String targetUUID = target.getStringUUID();
        float damage = grudges.getGrudge(targetUUID);
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
