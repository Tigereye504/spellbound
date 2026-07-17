package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
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
    public boolean isTreasureOnly() {return Spellbound.config.trophyCollector.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.trophyCollector.IS_FOR_SALE;}

    @Override
    public float getDamageBonus(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        float UniqueTrophyDamage = getUniqueDamageBonus(getUniqueTrophyCount(stack));
        int EntityTrophyDamage = 0;
        if(defender instanceof LivingEntity) {
            EntityTrophyDamage = getEntityDamageBonus(getEntityTrophyCount((LivingEntity)defender, stack));
        }
        return UniqueTrophyDamage + EntityTrophyDamage;
    }

    @Override
    public float getProjectileDamage(int level, ItemStack stack, AbstractArrow projectile, Entity attacker, Entity defender, float damage) {
        float UniqueTrophyDamage = getRangedUniqueDamageMultiple(getUniqueTrophyCount(stack));
        float EntityTrophyDamage = 0;
        if(defender instanceof LivingEntity) {
            EntityTrophyDamage = getRangedEntityDamageMultiple(getEntityTrophyCount((LivingEntity)defender, stack));
        }
        return damage*(1+(UniqueTrophyDamage + EntityTrophyDamage));
    }

    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        addTrophy(victim, killer, stack,stack.getItem() instanceof ProjectileWeaponItem);
    }

    @Override
    public void onLegacyToolBreak(int level, ItemStack book, ItemStack itemStack, Entity entity) {
        ItemStack bagOfTrophies = new ItemStack(SBItems.BAG_OF_TROPHIES);
        bagOfTrophies.addTagElement(TROPHY_COLLECTOR_KEY, itemStack.getTagElement(TROPHY_COLLECTOR_KEY));
        if(entity instanceof Player pEntity) {
            if (!pEntity.addItem(bagOfTrophies)) {
                entity.spawnAtLocation(bagOfTrophies, 0.5f);
            }
        }
        else{
            entity.spawnAtLocation(bagOfTrophies, 0.5f);
        }
    }

    @Override
    public List<Component> addTooltip(int level, ItemStack stack, Player player, TooltipFlag context) {
        return addTooltip(stack,player.level());
    }
    public List<Component> addTooltip(ItemStack stack, Level world) {
        boolean isRanged = stack.getItem() instanceof ProjectileWeaponItem;
        List<Component> output = new ArrayList<>();
        CompoundTag tag = stack.getOrCreateTagElement(TROPHY_COLLECTOR_KEY);
        Map<String,Integer> keyIntMap = getTrophyMap(stack);
        int trophyCount = getUniqueTrophyCount(stack);
        if(isRanged) {
            output.add(Component.literal(
                    "--" + trophyCount + " Unique Trophies (+"
                            + String.format("%.2f", getRangedUniqueDamageMultiple(getUniqueTrophyCount(stack))) + "x)--"));
        }
        else{
            output.add(Component.literal(
                    "--" + tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + " Unique Trophies (+"
                            + String.format("%.1f", getUniqueDamageBonus(getUniqueTrophyCount(stack))) + ")--"));
        }
        Stream<Map.Entry<String, Integer>> stream = keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        int scrollingSteps = Math.max(1,trophyCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(world.getGameTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry,isRanged));
        output.add(Component.literal("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Component> output, Map.Entry<String, Integer> entry, boolean isRanged){
        if(isRanged) {
            output.add(Component.literal(
                    entry.getValue() + " ")
                    .append(Component.translatable(entry.getKey()))
                    .append(" (+" + String.format("%.1f", getRangedEntityDamageMultiple(entry.getValue())) + "x)"));
        }
        else{
            output.add(Component.literal(
                    entry.getValue() + " ")
                    .append(Component.translatable(entry.getKey()))
                    .append(" (+" + getEntityDamageBonus(entry.getValue()) + ")"));
        }
    }

    private boolean hasTrophy(LivingEntity victim, ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(TROPHY_COLLECTOR_KEY);
        return tag.contains(victim.getType().toString());
    }

    private boolean addTrophy(LivingEntity victim, LivingEntity killer, ItemStack stack,boolean isRanged){
        CompoundTag tag = stack.getOrCreateTagElement(TROPHY_COLLECTOR_KEY);
        if(Spellbound.config.TAKE_ANY_TROPHY ||
                !(victim instanceof AgeableMob || victim instanceof WaterAnimal) || victim instanceof NeutralMob || victim instanceof Enemy) {
            if (!hasTrophy(victim, stack)) {
                tag.putInt(UNIQUE_TROPHY_COUNT_KEY, tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + 1);
                tag.putInt(victim.getType().toString(), 1);
                if (killer instanceof Player) {
                    String message = stack.getHoverName().getString()
                            + " acquired a "
                            + Component.translatable(victim.getType().toString()).getString()
                            + " trophy";
                    ((Player) killer).displayClientMessage(Component.literal(message)
                            , true);
                }
                return true;
            } else {
                int newValue = tag.getInt(victim.getType().toString()) + 1;
                tag.putInt(victim.getType().toString(), newValue);
                if(killer instanceof Player) {
                    if (isRanged) {
                        if (getRangedEntityDamageMultiple(newValue - 1) < (getRangedEntityDamageMultiple(newValue))) {
                            String message = stack.getHoverName().getString()
                                    + "'s "
                                    + Component.translatable(victim.getType().toString()).getString()
                                    + " trophy improved";
                            ((Player) killer).displayClientMessage(Component.literal(message)
                                    , true);
                        }
                    } else {
                        if (getEntityDamageBonus(newValue - 1) < (getEntityDamageBonus(newValue))) {
                            String message = stack.getHoverName().getString()
                                    + "'s "
                                    + Component.translatable(victim.getType().toString()).getString()
                                    + " trophy improved";
                            ((Player) killer).displayClientMessage(Component.literal(message)
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
        CompoundTag tag = stack.getOrCreateTagElement(TROPHY_COLLECTOR_KEY);
        return tag.getInt(UNIQUE_TROPHY_COUNT_KEY);
    }

    private int getEntityTrophyCount(LivingEntity victim, ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(TROPHY_COLLECTOR_KEY);
        return tag.getInt(victim.getType().toString());
    }

    public Map<String,Integer> getTrophyMap(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTagElement(TROPHY_COLLECTOR_KEY);
        Set<String> keys = tag.getAllKeys();
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
