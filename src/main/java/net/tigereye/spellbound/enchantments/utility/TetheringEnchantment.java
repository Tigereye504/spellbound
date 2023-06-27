package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.TetheredInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class TetheringEnchantment extends SBEnchantment {

    public TetheringEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.tethering.RARITY), EnchantmentTarget.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.tethering.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.tethering.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.tethering.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.tethering.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.tethering.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.tethering.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.tethering.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.tethering.IS_FOR_SALE;}

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
