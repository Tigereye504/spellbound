package net.tigereye.spellbound.enchantments.utility;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SpellboundUtil;

public class TetheringEnchantment extends SBEnchantment {

    public TetheringEnchantment() {
        super(definition(ItemTags.WEAPON_ENCHANTABLE, ItemTags.TRIDENT_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.airline.RARITY), //enchantment weight
            Spellbound.config.airline.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.airline.BASE_POWER,Spellbound.config.airline.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.airline.BASE_POWER+Spellbound.config.airline.POWER_RANGE,Spellbound.config.airline.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.airline.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.tethering.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.tethering.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.tethering.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.tethering.IS_FOR_SALE;}

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
        ((SpellboundLivingEntity)target).spellbound$setLastTether(anchor.getUUID());
        target.addEffect(new MobEffectInstance(SBStatusEffects.TETHERED, 20+(20*level), 0));
    }

    //doesn't support bows/crossbows because arrows usually dont survive impact
}
