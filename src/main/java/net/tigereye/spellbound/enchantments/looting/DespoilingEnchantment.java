package net.tigereye.spellbound.enchantments.looting;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

public class DespoilingEnchantment extends SBEnchantment{

    public DespoilingEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE,
            Spellbound.config.despoiling.WEIGHT, //enchantment weight
            Spellbound.config.despoiling.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.despoiling.BASE_POWER,Spellbound.config.despoiling.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.despoiling.BASE_POWER+Spellbound.config.despoiling.POWER_RANGE,Spellbound.config.despoiling.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.despoiling.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.despoiling.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.despoiling.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.despoiling.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.despoiling.IS_FOR_SALE;}

    @Override
    public int getLootingValue(int level, LivingEntity user, ItemStack stack) {
        MobEffectInstance greenSparkles = user.getEffect(SBStatusEffects.GREEN_SPARKLES);
        if(greenSparkles != null){
            return level*2;
        }
        return 0;
    }
    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        killer.addEffect(new MobEffectInstance(SBStatusEffects.GREEN_SPARKLES,
                Spellbound.config.despoiling.DURATION_BASE +(Spellbound.config.despoiling.DURATION_PER_LEVEL*level),
                level-1));
    }
}
