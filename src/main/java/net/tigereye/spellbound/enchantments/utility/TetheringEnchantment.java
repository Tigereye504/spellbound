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
import net.tigereye.spellbound.interfaces.UtilityEnchantment;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class TetheringEnchantment extends SBEnchantment implements UtilityEnchantment {

    public TetheringEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return 5+(level*10);
    }

    @Override
    public int getMaxPower(int level) {
        return getMinPower(level)+20;
    }

    @Override
    public int getMaxLevel() {
        if(Spellbound.config.TETHERING_ENABLED) return 3;
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

    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other) && !(other instanceof UtilityEnchantment);
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        //Spellbound.LOGGER.info("Applying Tethered");
        target.addStatusEffect(new TetheredInstance(anchor, 20+(20*level), 0));
    }

    //doesn't support bows/crossbows because arrows usually dont survive impact
}
