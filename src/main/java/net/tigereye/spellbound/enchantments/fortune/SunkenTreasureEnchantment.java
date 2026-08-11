package net.tigereye.spellbound.enchantments.fortune;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.tigereye.modifydropsapi.api.GenerateLootCallbackAddLoot;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import net.tigereye.spellbound.data.SunkenTreasure.SunkenTreasureManager;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBItems;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.List;

public class SunkenTreasureEnchantment extends SBEnchantment {


    public SunkenTreasureEnchantment() {
        super(definition(ItemTags.FISHING_ENCHANTABLE,
            Spellbound.config.sunkenTreasure.WEIGHT, //enchantment weight
            Spellbound.config.sunkenTreasure.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.sunkenTreasure.BASE_POWER,Spellbound.config.sunkenTreasure.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.sunkenTreasure.BASE_POWER+Spellbound.config.sunkenTreasure.POWER_RANGE,Spellbound.config.sunkenTreasure.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.sunkenTreasure.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.sunkenTreasure.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.sunkenTreasure.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.sunkenTreasure.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.sunkenTreasure.IS_FOR_SALE;}

    public static void registerSunkenTreasureCrateFishing(){
        GenerateLootCallbackAddLoot.EVENT.register((type, lootContext) -> {
            List<ItemStack> loot = new ArrayList<>();
            if(!lootContext.getLevel().isClientSide && type == LootContextParamSets.FISHING) {
                ItemStack tool = lootContext.getParamOrNull(LootContextParams.TOOL);
                if (tool != null) {
                    int level = EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.SUNKEN_TREASURE, tool);
                    if(lootContext.getRandom().nextFloat() < (level * Spellbound.config.sunkenTreasure.CRATE_CHANCE_PER_LEVEL / 2.0)){ //The odds are halved as a workaround to a bug where fishing (and only fishing) procs modify drops API twice
                        ItemStack crate = new ItemStack(SBItems.CRATE);
                        CompoundTag blockEntityTag = new CompoundTag();
                        blockEntityTag.putString(CrateBlockEntity.LOOT_DIMENSION_KEY,lootContext.getLevel().dimension().location().toString());
                        blockEntityTag.putInt(CrateBlockEntity.LOOT_QUALITY_KEY, SunkenTreasureManager.getWeightedRandomQuality(lootContext.getRandom(),lootContext.getLuck()));
                        BlockItem.setBlockEntityData(crate,SBItems.CRATE_BLOCK_ENTITY,blockEntityTag);
                        loot.add(crate);
                    }
                }
            }
            return loot;
        });
    }
}
