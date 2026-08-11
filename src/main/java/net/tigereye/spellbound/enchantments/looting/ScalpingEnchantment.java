package net.tigereye.spellbound.enchantments.looting;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;

public class ScalpingEnchantment extends SBEnchantment{

    public ScalpingEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE, SBTags.AXE_ENCHANTABLE,
            Spellbound.config.scalping.WEIGHT, //enchantment weight
            Spellbound.config.scalping.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.scalping.BASE_POWER,Spellbound.config.scalping.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.scalping.BASE_POWER+Spellbound.config.scalping.POWER_RANGE,Spellbound.config.scalping.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.scalping.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }
    
    @Override
    public boolean isEnabled() {return Spellbound.config.scalping.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.scalping.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.scalping.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.scalping.IS_FOR_SALE;}

    @Override
    public void onDoRedHealthDamage(int level, ItemStack itemStack, LivingEntity attacker, LivingEntity victim, DamageSource source, float amount) {
        if(victim.level().isClientSide()){
            return;
        }
        ResourceKey<LootTable> lootTableKey = victim.getLootTable();
        LootTable lootTable = victim.level().getServer().reloadableRegistries().getLootTable(lootTableKey);
        LootParams.Builder builder = new LootParams.Builder((ServerLevel) victim.level())
                .withParameter(LootContextParams.KILLER_ENTITY,attacker)
                .withParameter(LootContextParams.ORIGIN,attacker.position())
                .withParameter(LootContextParams.THIS_ENTITY,victim)
                .withParameter(LootContextParams.DAMAGE_SOURCE,source);
        float dropChance = (amount / victim.getMaxHealth()) * level * Spellbound.config.scalping.DROP_FACTOR_PER_LEVEL;
        while(dropChance > 0){
            List<ItemStack> rawItemDrops = lootTable.getRandomItems(builder.create(LootContextParamSets.ENTITY));
            if(dropChance < 1) {
                float finalDropChance = dropChance;
                rawItemDrops.removeIf((ItemStack) -> attacker.getRandom().nextFloat() > finalDropChance);
            }
            for (ItemStack stack:
                 rawItemDrops) {
                victim.spawnAtLocation(stack);
            }
            dropChance = (dropChance - 1) * Spellbound.config.scalping.CARRYOVER_DECAY;
        }
    }
}
