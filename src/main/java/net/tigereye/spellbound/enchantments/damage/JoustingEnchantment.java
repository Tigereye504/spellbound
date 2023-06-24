package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.util.SpellboundUtil;

public class JoustingEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    public JoustingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.JOUSTING_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.JOUSTING_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.JOUSTING_POWER_PER_RANK * level) + Spellbound.config.JOUSTING_BASE_POWER;
        if(level > Spellbound.config.JOUSTING_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.JOUSTING_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.JOUSTING_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack)
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

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof TridentItem
                || stack.getItem() == Items.BOOK;
    }
}
