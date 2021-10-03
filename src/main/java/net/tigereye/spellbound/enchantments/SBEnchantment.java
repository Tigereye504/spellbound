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
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public abstract class SBEnchantment extends Enchantment {
    protected boolean REQUIRES_PREFERRED_SLOT = true;

    protected SBEnchantment(Rarity weight, EnchantmentTarget type, EquipmentSlot[] slotTypes) {
        super(weight, type, slotTypes);
    }

    public abstract boolean isEnabled();

    //returned float is added to attack damage. Negatives work, the final damage floors at 0 though.
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        return 0;
    }

    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        return miningSpeed;
    }

    public void onActivate(int level, PlayerEntity playerEntity, ItemStack itemStack, Entity target) {}

    //for when equipment is changed
    public void onEquipmentChange(int level, ItemStack stack, LivingEntity entity){}

    //public void onArmorChangeEvenIfAbsent

    public void onBreakBlock(int level, ItemStack itemStack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
    }

    //for when the user dies
    public void onDeath(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){}

    //for when the user jumps
    public void onJump(int level, ItemStack stack, LivingEntity entity){}

    //for when the user kills
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){}

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

    //for every tick the enchanted item is equipped.
    // Careful, this will be called separately for every instance of the enchantment.
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){}

    public void onTickAlways(LivingEntity entity){}

    public void onToolBreak(int level, ItemStack itemStack, PlayerEntity entity) {
    }

    public float getProtectionAmount(int level, DamageSource source, ItemStack stack, LivingEntity target) {
        return 0;
    }

    public List<Text> addTooltip(int level, ItemStack itemStack, PlayerEntity player, TooltipContext context) {
        return new ArrayList<>();
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
}
