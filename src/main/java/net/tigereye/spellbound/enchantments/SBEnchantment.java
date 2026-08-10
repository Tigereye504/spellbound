package net.tigereye.spellbound.enchantments;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.List;

public abstract class SBEnchantment extends Enchantment {
    protected boolean REQUIRES_PREFERRED_SLOT;

    protected SBEnchantment(EnchantmentDefinition definition, boolean requiresPreferedSlot) {
        super(definition);
        REQUIRES_PREFERRED_SLOT = requiresPreferedSlot;
    }
    public abstract boolean isEnabled();
    public abstract int getSoftLevelCap();

    public int getPriority(){return 0;}

    @Override
    public int getMinCost(int level) {
        int power = super.getMinCost(level);
        if(level > getSoftLevelCap()) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxCost(int level) {
        int power = super.getMaxCost(level);
        if(level > getSoftLevelCap()) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return super.getMaxLevel();
        else return 0;
    }

    //triggers after unbreaking. Recieves remaining durability to be lost,
    //and return value determines how much will actually be lost.
    //Intended for unbreaking alternatives.
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayer user, int loss){return loss;}

    //returned float is added to attack damage. Negatives work, the final damage floors at 0 though.
    public float getDamageBonus(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        return 0;
    }


    public float getBaseMiningSpeed(int level, ItemStack itemStack, BlockState block, Float miningSpeed) {
        return miningSpeed;
    }

    //called when a tool is used to dig. receives and returns mining speed
    public float getMiningSpeed(int level, Player playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        return miningSpeed;
    }

    public int getLootingValue(int level, LivingEntity user, ItemStack stack) {
        return 0;
    }
    public void onActivate(int level, Player playerEntity, ItemStack itemStack, Entity target) {}

    //for when you reel in a hooked entity
    public void onPullHookedEntity(int level, FishingHook bobber, ItemStack stack, LivingEntity user, Entity target){}

    //public void onArmorChangeEvenIfAbsent

    public void onBreakBlockDirectly(int level, ItemStack itemStack, Level world, BlockPos pos, BlockState state, Player player) {
        onBreakBlock(level, itemStack, world, pos, state, player);
    }

    public void onBreakBlock(int level, ItemStack itemStack, Level world, BlockPos pos, BlockState state, Player player) {}

    public boolean onLethalDamageOnce(int level, DamageSource source, LivingEntity entity){return false;}
    public boolean onLethalDamage(int level, DamageSource source, LivingEntity entity){return false;}
    //for when the user dies
    public void onDeath(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){}

    //for when arrows are fired
    public void onFireProjectile(int level, ItemStack itemStack, Entity entity, Projectile projectile){}

    //for every tick while the item is in a player's inventory
    public void onInventoryTick(int level, ItemStack stack, Level world, Entity entity, int slot, boolean selected){}

    //for when the user jumps
    public void onJump(int level, ItemStack stack, LivingEntity entity){}

    //for when the user double jumps
    public void onMidairJump(int level, ItemStack stack, LivingEntity entity){}

    //for when the user kills
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){}

    //for when an item breaks with Legacy
    public void onLegacyToolBreak(int level, ItemStack book, ItemStack itemStack, Entity entity) {}

    //for when the user is struck, before armor is applied
    public float onPreArmorDefense(int level, ItemStack stack, DamageSource source, LivingEntity defender, float amount){return amount;}

    public void onStartSleepingAlways(LivingEntity entity){}

    //for when a thrown trident strikes a target
    //called before vanilla on-hit but after vanilla on-hurt
    public void onThrownTridentEntityHit(int level, ThrownTrident tridentEntity, ItemStack tridentItem, Entity defender){}

    //for when a thrown trident strikes a target
    //called before vanilla on-hit but after vanilla on-hurt
    public float getThrownTridentDamage(int level, ThrownTrident tridentEntity, ItemStack tridentItem, Entity defender){
        if(tridentEntity.getOwner() instanceof LivingEntity){
            return getDamageBonus(level,tridentItem,(LivingEntity)tridentEntity.getOwner(),defender);
        }
        return 0;
    }

    //for when trident thrown
    public void onThrowTrident(int level, ItemStack itemStack, Entity entity, ThrownTrident projectile){}

    //for every tick the enchanted item is equipped.
    // Careful, this will be called separately for every instance of the enchantment.
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){}

    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){}

    public void onTickAlways(LivingEntity entity){}

    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){}

    public boolean beforeToolBreak(int level, ItemStack itemStack, Entity entity) {return true;}

    public void onToolBreak(int level, ItemStack itemStack, Entity entity) {}

    public float getArmorAmount(int level, ItemStack stack, LivingEntity entity) {
        return 0;
    }

    public float getLocalDifficultyModifier(int level, Level world, Player player, ItemStack itemStack) {
        return 0;
    }
    public float getProtectionAmount(int level, DamageSource source, ItemStack stack, LivingEntity target) {
        return 0;
    }

    public int getIFrameAmount(int level, int frames, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return frames;
    }

    public float getIFrameMagnitude(int level, float magnitude, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return magnitude;
    }

    public List<Component> addTooltip(int level, ItemStack itemStack, Player player, TooltipFlag context) {
        return null;
    }

    public float getProjectileDamage(int level, ItemStack stack, AbstractArrow projectile, Entity attacker, Entity victim, float damage) {
        return damage;
    }

    public void onTargetDamaged(int level, ItemStack itemStack, LivingEntity user, Entity entity){}

    public void onProjectileEntityHit(int level, ItemStack itemStack, AbstractArrow persistentProjectileEntity, Entity entity) {
    }

    public void onProjectileBlockHit(int level, ItemStack itemStack, Projectile projectileEntity, BlockHitResult blockHitResult) {
    }

    public void onTakeRedHealthDamage(int level, ItemStack itemStack, DamageSource source, LivingEntity entity, float amount) {
    }

    public void onTakeRedHealthDamageOnce(int level, ItemStack itemStack, DamageSource source, LivingEntity entity, float amount) {}

    public void afterHeal(int level, ItemStack itemStack, LivingEntity entity, float amount) {
    }

    public void afterHealOnce(int level, ItemStack itemStack, LivingEntity entity, float amount) {}


    public void onDoRedHealthDamage(int level, ItemStack itemStack, LivingEntity attacker, LivingEntity victim, DamageSource source, float amount) {
    }

    public void onStatusEffectsCleared(int level, ItemStack itemStack, LivingEntity owner) {
    }

    public boolean requiresPreferredSlot(){
        return REQUIRES_PREFERRED_SLOT;
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return (super.canEnchant(stack) || stack.getItem() == Items.BOOK) && isEnabled();
    }

    @Override
    public boolean isTradeable() {
        return isEnabled();
    }

    @Override
    public boolean isDiscoverable() {
        return isEnabled();
    }

    @Override
    public boolean checkCompatibility(Enchantment other) {
        return super.checkCompatibility(other) && (SBEnchantmentHelper.areNotInSameCategory(this,other)
                || Spellbound.config.DISABLE_INCOMPATIBILITY);
    }

    public void onGainExperienceAlways(Player player, int amount) {}

    public void onItemUse(int level, ItemStack itemStack, UseOnContext context, InteractionResult result) {
    }

    public boolean setItemSuitability(int level, ItemStack stack, BlockState state, Boolean suitability) {
        return suitability;
    }

    public boolean onClientEntityIsGlowing(int level, ItemStack itemStack, LocalPlayer player, Entity entity, Boolean isGlowing) {
        return isGlowing;
    }

    public int overwriteClientEntityTeamColor(int level, ItemStack itemStack, LocalPlayer player, Entity entity, int color) {
        return color;
    }
}
