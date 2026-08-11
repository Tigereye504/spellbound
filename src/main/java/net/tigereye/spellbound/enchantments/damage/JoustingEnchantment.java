package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class JoustingEnchantment extends SBEnchantment{

    public JoustingEnchantment() {
        super(definition(SBTags.JOUSTING_ENCHANTABLE, ItemTags.TRIDENT_ENCHANTABLE, //enchantment targets: supports shovels and melee weapons, primarily for tridents
            Spellbound.config.jousting.WEIGHT, //enchantment weight
            Spellbound.config.jousting.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.jousting.BASE_POWER,Spellbound.config.jousting.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.jousting.BASE_POWER+Spellbound.config.jousting.POWER_RANGE,Spellbound.config.jousting.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.jousting.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.jousting.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.jousting.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.jousting.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.jousting.IS_FOR_SALE;}

    @Override
    public float getDamageBonus(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        Vec3 attackerOldPos = ((SpellboundLivingEntity)attacker).spellbound$readPositionTracker();
        if(attackerOldPos == null){
            Spellbound.LOGGER.error("Unable to read Jousting attacker's old position!");
            Spellbound.LOGGER.error("Attacker: "+attacker);
            Spellbound.LOGGER.error("Defender: "+defender);
            Spellbound.LOGGER.error("Weapon: "+stack);
            return 0;
        }
        Vec3 attackerVelocity = attacker.position().subtract(attackerOldPos);
        Vec3 relativeVelocity = attackerVelocity.subtract(defender.getDeltaMovement());
        Vec3 attackerFacing = attacker.getLookAngle().normalize();
        double dotP = relativeVelocity.dot(attackerFacing);
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
        return damage;
        //return (float)dotP*10*level;
    }

    @Override
    public void onTickWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        ((SpellboundLivingEntity)entity).spellbound$updatePositionTracker(entity.position());
    }
}
