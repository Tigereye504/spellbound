package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SpellboundUtil;

public class JoustingEnchantment extends SBEnchantment{

    public JoustingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.jousting.RARITY), EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.jousting.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){
        return Spellbound.config.jousting.SOFT_CAP;
    }
    @Override
    public int getHardLevelCap(){
        return Spellbound.config.jousting.HARD_CAP;
    }
    @Override
    public int getBasePower(){
        return Spellbound.config.jousting.BASE_POWER;
    }
    @Override
    public int getPowerPerRank(){
        return Spellbound.config.jousting.POWER_PER_RANK;
    }
    @Override
    public int getPowerRange(){
        return Spellbound.config.jousting.POWER_RANGE;
    }
    @Override
    public boolean isTreasure() {return Spellbound.config.jousting.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.jousting.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack)
                || EnchantmentTarget.WEAPON.isAcceptableItem(stack.getItem())
                || stack.getItem() instanceof ShovelItem
                || stack.getItem() instanceof AxeItem;
    }

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        Vec3d attackerVelocity = attacker.getPos().subtract(((SpellboundLivingEntity)attacker).readPositionTracker());
        Vec3d relativeVelocity = attackerVelocity.subtract(defender.getVelocity());
        Vec3d attackerFacing = attacker.getRotationVector().normalize();
        double dotP = relativeVelocity.dotProduct(attackerFacing);
        float damage;
        if(Math.abs(dotP) < .2){
            damage = 0;
        }
        else {
            damage = (float) (Math.log(((Math.abs(dotP)-.2) / .15) + 1) / Math.log(3)) * 3 * level;
            if (damage != damage) {
                damage = 0;
            }
            if (dotP < 0) {
                damage = -damage;
            }
        }
        if(Spellbound.DEBUG && attacker instanceof PlayerEntity && !attacker.world.isClient){
            String out;
            out = "Dot Product: " + dotP;
            ((PlayerEntity)attacker).sendMessage(Text.literal(out), false);
            out = "Damage: " + damage;
            ((PlayerEntity)attacker).sendMessage(Text.literal(out), false);
            out = "Relative Velocity: " + relativeVelocity;
            ((PlayerEntity)attacker).sendMessage(Text.literal(out), false);
            out = "Attacker Facing: " + attackerFacing;
            ((PlayerEntity)attacker).sendMessage(Text.literal(out), false);
            out = "Attacker Velocity: " + attackerVelocity;
            ((PlayerEntity)attacker).sendMessage(Text.literal(out), false);
            out = "Defender Velocity: " + defender.getVelocity();
            ((PlayerEntity)attacker).sendMessage(Text.literal(out), false);
        }
        return damage;
        //return (float)dotP*10*level;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        ((SpellboundLivingEntity)entity).updatePositionTracker(entity.getPos());
    }
}
