package net.tigereye.spellbound.enchantments.fortune;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
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
        super(SpellboundUtil.rarityLookup(Spellbound.config.sunkenTreasure.RARITY), EnchantmentCategory.FISHING_ROD, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.sunkenTreasure.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.sunkenTreasure.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.sunkenTreasure.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.sunkenTreasure.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.sunkenTreasure.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.sunkenTreasure.POWER_RANGE;}
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
                        blockEntityTag.putString(CrateBlockEntity.LOOT_DIMENSION_KEY,lootContext.getLevel().dimensionTypeId().location().toString());
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
