package net.tigereye.spellbound.enchantments.looting;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;

public class ScalpingEnchantment extends SBEnchantment{

    public ScalpingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.scalping.RARITY), SBEnchantmentTargets.AXE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.scalping.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.scalping.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.scalping.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.scalping.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.scalping.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.scalping.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.scalping.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.scalping.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack)
                || SBEnchantmentTargets.ANY_WEAPON.isAcceptableItem(stack.getItem());
    }

    @Override
    public void onDoRedHealthDamage(int level, ItemStack itemStack, LivingEntity attacker, LivingEntity victim, DamageSource source, float amount) {
        if(victim.getWorld().isClient()){
            return;
        }
        Identifier identifier = victim.getLootTable();
        LootManager lootManager = victim.getWorld().getServer().getLootManager();
        LootTable lootTable = lootManager.getLootTable(identifier);
        LootContextParameterSet.Builder builder = new LootContextParameterSet.Builder((ServerWorld) victim.getWorld()).add(LootContextParameters.KILLER_ENTITY,attacker);
        float dropChance = (amount / victim.getMaxHealth()) * level * Spellbound.config.scalping.DROP_FACTOR_PER_LEVEL;
        while(dropChance > 0){
            List<ItemStack> rawItemDrops = lootTable.generateLoot(builder.build(LootContextTypes.ENTITY));
            if(dropChance < 1) {
                float finalDropChance = dropChance;
                rawItemDrops.removeIf((ItemStack) -> attacker.getRandom().nextFloat() > finalDropChance);
            }
            for (ItemStack stack:
                 rawItemDrops) {
                victim.dropStack(stack);
            }
            dropChance = (dropChance - 1) * Spellbound.config.scalping.CARRYOVER_DECAY;
        }
    }
}
