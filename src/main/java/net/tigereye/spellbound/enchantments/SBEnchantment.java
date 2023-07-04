package net.tigereye.spellbound.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.util.SBEnchantmentHelper;

import java.util.List;

public abstract class SBEnchantment extends Enchantment {
    protected boolean REQUIRES_PREFERRED_SLOT;

    protected SBEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes,boolean requiresPreferedSlot) {
        super(weight, type, slotTypes);
        REQUIRES_PREFERRED_SLOT = requiresPreferedSlot;
    }
    public abstract boolean isEnabled();
    public abstract int getSoftLevelCap();
    public abstract int getHardLevelCap();
    public abstract int getBasePower();
    public abstract int getPowerPerRank();
    public abstract int getPowerRange();

    @Override
    public int getMinPower(int level) {
        int power = (getPowerPerRank() * level) + getBasePower();
        if(level > getSoftLevelCap()) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + getPowerRange();
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return getHardLevelCap();
        else return 0;
    }

    //triggers after unbreaking. Recieves remaining durability to be lost,
    //and return value determines how much will actually be lost.
    //Intended for unbreaking alternatives.
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayerEntity user, int loss){return loss;}

    //returned float is added to attack damage. Negatives work, the final damage floors at 0 though.
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        return 0;
    }

    //called when a tool is used to dig. receives and returns mining speed
    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        return miningSpeed;
    }

    public void onActivate(int level, PlayerEntity playerEntity, ItemStack itemStack, Entity target) {}

    //for when equipment is changed
    //public void onEquipmentChange(int level, ItemStack stack, LivingEntity entity){}

    //for when you reel in a hooked entity
    public void onPullHookedEntity(int level, FishingBobberEntity bobber, ItemStack stack, LivingEntity user, Entity target){}

    //public void onArmorChangeEvenIfAbsent

    public void onBreakBlock(int level, ItemStack itemStack, World world, BlockPos pos, BlockState state, PlayerEntity player) {}

    //for when the user dies
    public void onDeath(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){}

    //for when arrows are fired
    public void onFireProjectile(int level, ItemStack itemStack, Entity entity, ProjectileEntity projectile){}

    //for every tick while the item is in a player's inventory
    public void onInventoryTick(int level, ItemStack stack, World world, Entity entity, int slot, boolean selected){}

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

    //for when a thrown trident strikes a target
    //called before vanilla on-hit but after vanilla on-hurt
    public void onThrownTridentEntityHit(int level, TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){}

    //for when a thrown trident strikes a target
    //called before vanilla on-hit but after vanilla on-hurt
    public float getThrownTridentDamage(int level, TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        if(tridentEntity.getOwner() instanceof LivingEntity){
            return getAttackDamage(level,tridentItem,(LivingEntity)tridentEntity.getOwner(),defender);
        }
        return 0;
    }

    //for when trident thrown
    public void onThrowTrident(int level, ItemStack itemStack, Entity entity, TridentEntity projectile){}

    //for every tick the enchanted item is equipped.
    // Careful, this will be called separately for every instance of the enchantment.
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){}

    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){}

    public void onTickAlways(LivingEntity entity){}

    public void onEquipmentChange(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){}

    public boolean beforeToolBreak(int level, ItemStack itemStack, Entity entity) {return true;}

    public void onToolBreak(int level, ItemStack itemStack, Entity entity) {}

    public float getArmorAmount(int level, ItemStack stack, LivingEntity entity) {
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

    public List<Text> addTooltip(int level, ItemStack itemStack, PlayerEntity player, TooltipContext context) {
        return null;
    }

    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity victim, float damage) {
        return damage;
    }

    public void onProjectileEntityHit(int level, ItemStack itemStack, PersistentProjectileEntity persistentProjectileEntity, Entity entity) {
    }

    public void onProjectileBlockHit(int level, ItemStack itemStack, ProjectileEntity projectileEntity, BlockHitResult blockHitResult) {
    }

    public void onRedHealthDamage(int level, ItemStack itemStack, LivingEntity entity, float amount) {
    }

    public void onDoRedHealthDamage(int level, ItemStack itemStack, LivingEntity attacker, LivingEntity victim, DamageSource source, float amount) {
    }

    public boolean requiresPreferredSlot(){
        return REQUIRES_PREFERRED_SLOT;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack) && isEnabled();
    }

    @Override
    public boolean isAvailableForEnchantedBookOffer() {
        return isEnabled();
    }

    @Override
    public boolean isAvailableForRandomSelection() {
        return isEnabled();
    }

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && (SBEnchantmentHelper.areNotInSameCategory(this,other)
                || Spellbound.config.DISABLE_INCOMPATIBILITY);
    }
}
