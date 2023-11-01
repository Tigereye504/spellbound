package net.tigereye.spellbound.enchantments.looting;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class DespoilingEnchantment extends SBEnchantment{

    public DespoilingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.despoiling.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.despoiling.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.despoiling.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.despoiling.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.despoiling.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.despoiling.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.despoiling.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.despoiling.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.despoiling.IS_FOR_SALE;}

    @Override
    public int getLootingValue(int level, LivingEntity user, ItemStack stack) {
        StatusEffectInstance greenSparkles = user.getStatusEffect(SBStatusEffects.GREEN_SPARKLES);
        if(greenSparkles != null){
            return level*2;
        }
        return 0;
    }
    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        killer.addStatusEffect(new StatusEffectInstance(SBStatusEffects.GREEN_SPARKLES,
                Spellbound.config.despoiling.DURATION_BASE +(Spellbound.config.despoiling.DURATION_PER_LEVEL*level),
                level-1));
    }
}
