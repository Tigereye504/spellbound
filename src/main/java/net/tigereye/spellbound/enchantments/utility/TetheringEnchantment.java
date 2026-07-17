package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.mob_effect.instance.OwnedStatusEffectInstance;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class TetheringEnchantment extends SBEnchantment {

    public TetheringEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.tethering.RARITY), EnchantmentCategory.TRIDENT, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
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
    public boolean isTreasureOnly() {return Spellbound.config.tethering.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.tethering.IS_FOR_SALE;}

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack)
                || stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || EnchantmentCategory.DIGGER.canEnchant(stack.getItem());
    }

    @Override
    public void onThrownTridentEntityHit(int level, ThrownTrident tridentEntity, ItemStack tridentItem, Entity defender){
        if(defender instanceof LivingEntity){
            tetherTarget(level, tridentEntity,(LivingEntity)defender);
        }
        super.onThrownTridentEntityHit(level,tridentEntity,tridentItem,defender);
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        //Spellbound.LOGGER.info("Tether Target Hit");
        if(target instanceof LivingEntity
                /*&& EnchantmentHelper.get(((LivingEntity) target).getMainHandStack()).containsKey(SBEnchantments.TETHERING)*/) {
            tetherTarget(level, user, (LivingEntity) target);
        }

        super.doPostAttack(user, target, level);
    }

    private void tetherTarget(int level, Entity anchor, LivingEntity target){
        target.removeEffect(SBStatusEffects.TETHERED);
        target.addEffect(new OwnedStatusEffectInstance(anchor, SBStatusEffects.TETHERED, 20+(20*level), 0));
    }

    //doesn't support bows/crossbows because arrows usually dont survive impact
}
