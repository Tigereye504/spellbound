package net.tigereye.spellbound.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.util.SpellboundUtil;

public class JoustingEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    public JoustingEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level*10);
    }

    @Override
    public int getMaxPower(int level) {
        return this.getMinPower(level)+15;
    }

    @Override
    public int getMaxLevel() {
        return 3;
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
        Vec3d attackerVelocity = getJoustingVelocity(attacker,stack);
        Vec3d relativeVelocity = attackerVelocity.subtract(defender.getVelocity());
        Vec3d attackerFacing = attacker.getRotationVector().normalize();
        double dotP = relativeVelocity.dotProduct(attackerFacing);
        if(Spellbound.DEBUG && attacker instanceof PlayerEntity && !attacker.world.isClient){
            String out;
            out = "Dot Product: " + dotP;
            ((PlayerEntity)attacker).sendMessage(new LiteralText(out), false);
            out = "Relative Velocity: " + relativeVelocity.toString();
            ((PlayerEntity)attacker).sendMessage(new LiteralText(out), false);
            out = "Attacker Facing: " + attackerFacing.toString();
            ((PlayerEntity)attacker).sendMessage(new LiteralText(out), false);
            out = "Attacker Velocity: " + attackerVelocity.toString();
            ((PlayerEntity)attacker).sendMessage(new LiteralText(out), false);
            out = "Defender Velocity: " + defender.getVelocity().toString();
            ((PlayerEntity)attacker).sendMessage(new LiteralText(out), false);
        }
        return (float)dotP*10*level;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        SpellboundUtil.setMotionTracker(stack,entity);
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    //I want to disallow damageEnchantments and anything else that disallows damageEnchantments
    //as typically the later is trying to be another form of damage enchantment
    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.SHARPNESS);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof TridentItem
                || stack.getItem() == Items.BOOK;
    }

    private Vec3d getJoustingVelocity(LivingEntity attacker, ItemStack stack){
        return SpellboundUtil.readMotionTracker(attacker,stack);
    }
}
