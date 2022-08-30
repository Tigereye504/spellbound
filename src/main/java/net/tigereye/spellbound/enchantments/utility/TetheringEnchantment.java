package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class TetheringEnchantment extends SBEnchantment {

    public TetheringEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.TETHERING_RARITY), EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.TETHERING_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.TETHERING_POWER_PER_RANK * level) - Spellbound.config.TETHERING_BASE_POWER;
        if(level > Spellbound.config.TETHERING_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.TETHERING_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.TETHERING_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack)
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || EnchantmentTarget.DIGGER.isAcceptableItem(stack.getItem());
    }

    @Override
    public void onThrownTridentEntityHit(int level, TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        if(defender instanceof LivingEntity){
            tetherTarget(level, tridentEntity,(LivingEntity)defender);
        }
        super.onThrownTridentEntityHit(level,tridentEntity,tridentItem,defender);
    }

    @Override
    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        //Spellbound.LOGGER.info("Tether Target Hit");
        if(target instanceof LivingEntity
                /*&& EnchantmentHelper.get(((LivingEntity) target).getMainHandStack()).containsKey(SBEnchantments.TETHERING)*/) {
            tetherTarget(level, user, (LivingEntity) target);
        }

        super.onTargetDamaged(user, target, level);
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        target.removeStatusEffect(SBStatusEffects.TETHERED);
        target.addStatusEffect(new TetheredInstance(anchor, 20+(20*level), 0));
    }

    //doesn't support bows/crossbows because arrows usually dont survive impact
}
