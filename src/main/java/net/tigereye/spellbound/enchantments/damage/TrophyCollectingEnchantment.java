package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBItems;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;
import java.util.stream.Stream;

public class TrophyCollectingEnchantment extends SBEnchantment{

    public static final String TROPHY_COLLECTOR_KEY = Spellbound.MODID+"TrophyCollector";
    public static final String UNIQUE_TROPHY_COUNT_KEY = Spellbound.MODID+"UniqueTrophyCount";

    public TrophyCollectingEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.trophyCollector.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.trophyCollector.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.trophyCollector.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.trophyCollector.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.trophyCollector.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.trophyCollector.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.trophyCollector.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.trophyCollector.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.trophyCollector.IS_FOR_SALE;}

    @Override
    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        float UniqueTrophyDamage = getUniqueDamageBonus(getUniqueTrophyCount(stack));
        int EntityTrophyDamage = 0;
        if(defender instanceof LivingEntity) {
            EntityTrophyDamage = getEntityDamageBonus(getEntityTrophyCount((LivingEntity)defender, stack));
        }
        return UniqueTrophyDamage + EntityTrophyDamage;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        float UniqueTrophyDamage = getRangedUniqueDamageMultiple(getUniqueTrophyCount(stack));
        float EntityTrophyDamage = 0;
        if(defender instanceof LivingEntity) {
            EntityTrophyDamage = getRangedEntityDamageMultiple(getEntityTrophyCount((LivingEntity)defender, stack));
        }
        return damage*(1+(UniqueTrophyDamage + EntityTrophyDamage));
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        addTrophy(victim, killer, stack,stack.getItem() instanceof RangedWeaponItem);
    }

    @Override
    public void onLegacyToolBreak(int level, ItemStack book, ItemStack itemStack, Entity entity) {
        ItemStack bagOfTrophies = new ItemStack(SBItems.BAG_OF_TROPHIES);
        bagOfTrophies.setSubNbt(TROPHY_COLLECTOR_KEY, itemStack.getSubNbt(TROPHY_COLLECTOR_KEY));
        if(entity instanceof PlayerEntity pEntity) {
            if (!pEntity.giveItemStack(bagOfTrophies)) {
                entity.dropStack(bagOfTrophies, 0.5f);
            }
        }
        else{
            entity.dropStack(bagOfTrophies, 0.5f);
        }
    }

    @Override
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context) {
        return addTooltip(stack,player.world);
    }
    public List<Text> addTooltip(ItemStack stack, World world) {
        boolean isRanged = stack.getItem() instanceof RangedWeaponItem;
        List<Text> output = new ArrayList<>();
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        Map<String,Integer> keyIntMap = getTrophyMap(stack);
        int trophyCount = getUniqueTrophyCount(stack);
        if(isRanged) {
            output.add(Text.literal(
                    "--" + trophyCount + " Unique Trophies (+"
                            + String.format("%.2f", getRangedUniqueDamageMultiple(getUniqueTrophyCount(stack))) + "x)--"));
        }
        else{
            output.add(Text.literal(
                    "--" + tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + " Unique Trophies (+"
                            + String.format("%.1f", getUniqueDamageBonus(getUniqueTrophyCount(stack))) + ")--"));
        }
        Stream<Map.Entry<String, Integer>> stream = keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int scrollingSteps = Math.max(1,trophyCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(world.getTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry,isRanged));
        output.add(Text.literal("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Text> output, Map.Entry<String, Integer> entry, boolean isRanged){
        if(isRanged) {
            output.add(Text.literal(
                    entry.getValue() + " ")
                    .append(Text.translatable(entry.getKey()))
                    .append(" (+" + String.format("%.1f", getRangedEntityDamageMultiple(entry.getValue())) + "x)"));
        }
        else{
            output.add(Text.literal(
                    entry.getValue() + " ")
                    .append(Text.translatable(entry.getKey()))
                    .append(" (+" + getEntityDamageBonus(entry.getValue()) + ")"));
        }
    }

    private boolean hasTrophy(LivingEntity victim, ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        return tag.contains(victim.getType().toString());
    }

    private boolean addTrophy(LivingEntity victim, LivingEntity killer, ItemStack stack,boolean isRanged){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        if(Spellbound.config.TAKE_ANY_TROPHY ||
                !(victim instanceof PassiveEntity || victim instanceof WaterCreatureEntity) || victim instanceof Angerable || victim instanceof Monster) {
            if (!hasTrophy(victim, stack)) {
                tag.putInt(UNIQUE_TROPHY_COUNT_KEY, tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + 1);
                tag.putInt(victim.getType().toString(), 1);
                if (killer instanceof PlayerEntity) {
                    String message = stack.getName().getString()
                            + " acquired a "
                            + Text.translatable(victim.getType().toString()).getString()
                            + " trophy";
                    ((PlayerEntity) killer).sendMessage(Text.literal(message)
                            , true);
                }
                return true;
            } else {
                int newValue = tag.getInt(victim.getType().toString()) + 1;
                tag.putInt(victim.getType().toString(), newValue);
                if(killer instanceof PlayerEntity) {
                    if (isRanged) {
                        if (getRangedEntityDamageMultiple(newValue - 1) < (getRangedEntityDamageMultiple(newValue))) {
                            String message = stack.getName().getString()
                                    + "'s "
                                    + Text.translatable(victim.getType().toString()).getString()
                                    + " trophy improved";
                            ((PlayerEntity) killer).sendMessage(Text.literal(message)
                                    , true);
                        }
                    } else {
                        if (getEntityDamageBonus(newValue - 1) < (getEntityDamageBonus(newValue))) {
                            String message = stack.getName().getString()
                                    + "'s "
                                    + Text.translatable(victim.getType().toString()).getString()
                                    + " trophy improved";
                            ((PlayerEntity) killer).sendMessage(Text.literal(message)
                                    , true);
                        }
                    }
                }
                return false;
            }
        }
        return false;
    }

    private int getUniqueTrophyCount(ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        return tag.getInt(UNIQUE_TROPHY_COUNT_KEY);
    }

    private int getEntityTrophyCount(LivingEntity victim, ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        return tag.getInt(victim.getType().toString());
    }

    public Map<String,Integer> getTrophyMap(ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        Set<String> keys = tag.getKeys();
        Map<String,Integer> keyIntMap = new HashMap<>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_TROPHY_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        return keyIntMap;
    }

    private float getUniqueDamageBonus(int uniques){
        return (float)Math.sqrt(uniques)/1.5f;
    }

    private int getEntityDamageBonus(int kills){
        int bonus = -1;
        while(kills > 4){
            ++bonus;
            kills /= 4;
        }
        return Math.max(bonus,0);
        //return (int) (Math.sqrt(kills) / 4);
    }

    private float getRangedUniqueDamageMultiple(int uniques){
        return (float)Math.sqrt(uniques)*0.25f;
    }

    private float getRangedEntityDamageMultiple(int kills){
        return (getEntityDamageBonus(kills))*0.25f;
    }
}
