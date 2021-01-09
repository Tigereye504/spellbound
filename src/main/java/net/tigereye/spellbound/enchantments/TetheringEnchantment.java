package net.tigereye.spellbound.enchantments;

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
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;

public class TetheringEnchantment extends SBEnchantment {

    public TetheringEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 5+(level*10);
    }

    public int getMaxPower(int level) {
        return getMinPower(level)+20;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack)
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || EnchantmentTarget.DIGGER.isAcceptableItem(stack.getItem());
    }

    public void onThrownTridentEntityHit(int level, TridentEntity tridentEntity, ItemStack tridentItem, Entity defender){
        if(defender instanceof LivingEntity){
            tetherTarget(level, tridentEntity,(LivingEntity)defender);
        }
        super.onThrownTridentEntityHit(level,tridentEntity,tridentItem,defender);
    }

    public void onTargetDamaged(LivingEntity user, Entity target, int level) {
        if(target instanceof LivingEntity
                && EnchantmentHelper.get(((LivingEntity) target).getMainHandStack()).containsKey(SBEnchantments.TETHERING)) {
            tetherTarget(level, user, (LivingEntity) target);
        }

        super.onTargetDamaged(user, target, level);
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        target.applyStatusEffect(new TetheredInstance(anchor, 20+(20*level), 0));
    }

    //TODO: add support for bows/crossbows
}
