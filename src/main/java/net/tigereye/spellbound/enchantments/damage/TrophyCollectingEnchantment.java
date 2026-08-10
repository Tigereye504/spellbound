package net.tigereye.spellbound.enchantments.damage;

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
import net.tigereye.spellbound.components.TrophyCollectionComponent;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBItems;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.*;
import java.util.stream.Stream;

public class TrophyCollectingEnchantment extends SBEnchantment{

    public static final String TROPHY_COLLECTOR_KEY = Spellbound.MODID+":trophy_collector";
    public static final String UNIQUE_TROPHY_COUNT_KEY = Spellbound.MODID+":unique_trophy_count";

    public TrophyCollectingEnchantment() {
        super(definition(SBTags.ALL_WEAPONS_ENCHANTABLE, //enchantment targets: ALL weapons, both melee and ranged
            SpellboundUtil.rarityLookup(Spellbound.config.trophyCollector.RARITY), //enchantment weight
            Spellbound.config.trophyCollector.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.trophyCollector.BASE_POWER,Spellbound.config.trophyCollector.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.trophyCollector.BASE_POWER+Spellbound.config.trophyCollector.POWER_RANGE,Spellbound.config.trophyCollector.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.trophyCollector.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            true); //can work outside of prefered slot
        }

    @Override
    public boolean isEnabled() {return Spellbound.config.trophyCollector.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.trophyCollector.SOFT_CAP;}
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
        if(itemStack.has(SBComponents.TROPHY_COLECTION)){
            ItemStack bagOfTrophies = new ItemStack(SBItems.BAG_OF_TROPHIES);
            bagOfTrophies.set(SBComponents.TROPHY_COLECTION,itemStack.get(SBComponents.TROPHY_COLECTION));
            if(entity instanceof Player pEntity) {
                if (!pEntity.addItem(bagOfTrophies)) {
                    entity.spawnAtLocation(bagOfTrophies, 0.5f);
                }
            }
            else{
                entity.spawnAtLocation(bagOfTrophies, 0.5f);
            }
        }
    }

    @Override
    public List<Component> addTooltip(int level, ItemStack stack, Player player, TooltipFlag context) {
        return addTooltip(stack,player.level());
    }
    public List<Component> addTooltip(ItemStack stack, Level world) {
        boolean isRanged = stack.getItem() instanceof ProjectileWeaponItem;

        List<Component> output = new ArrayList<>();
        List<TrophyCollectionComponent.Entry> orderedList = getOrderedTrophyList(stack);
        int trophyCount = getUniqueTrophyCount(stack);
        if(isRanged) {
            output.add(Component.literal(
                    "--" + trophyCount + " Unique Trophies (+"
                            + String.format("%.2f", getRangedUniqueDamageMultiple(getUniqueTrophyCount(stack))) + "x)--"));
        }
        else{
            output.add(Component.literal(
                    "--" + trophyCount + " Unique Trophies (+"
                            + String.format("%.1f", getUniqueDamageBonus(getUniqueTrophyCount(stack))) + ")--"));
        }
        Stream<TrophyCollectionComponent.Entry> stream = orderedList.stream();
        int scrollingSteps = Math.max(1,trophyCount-Spellbound.config.COLLECTOR_WINDOW_SIZE+1);
        if(scrollingSteps > 1) {
            stream = stream.skip(world.getGameTime() % ((long) scrollingSteps * Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD)) / Math.max(1,Spellbound.config.COLLECTOR_DISPLAY_UPDATE_PERIOD));
        }
        stream = stream.limit(Spellbound.config.COLLECTOR_WINDOW_SIZE);
        stream.forEach((entry) -> writeLineInTooltip(output,entry,isRanged));
        output.add(Component.literal("--------------------------"));
        return output;
    }

    private void writeLineInTooltip(List<Component> output, TrophyCollectionComponent.Entry entry, boolean isRanged){
        output.add(Component.literal(
            entry.count() + " ")
            .append(Component.translatable(entry.entityType()))
            .append( isRanged 
                ? " (+" + String.format("%.1f", getRangedEntityDamageMultiple(entry.count())) + "x)" 
                : " (+" + getEntityDamageBonus(entry.count()) + ")"));
    }

    private boolean hasTrophy(LivingEntity victim, ItemStack stack){
        if(!stack.has(SBComponents.TROPHY_COLECTION)){
            return false;
        }
        return stack.get(SBComponents.TROPHY_COLECTION).hasTrophy(victim.getType());
    }

    private void addTrophy(LivingEntity victim, LivingEntity killer, ItemStack stack,boolean isRanged){
        if(Spellbound.config.TAKE_ANY_TROPHY ||
                !(victim instanceof AgeableMob || victim instanceof WaterAnimal) || victim instanceof NeutralMob || victim instanceof Enemy) {
            boolean newTrophy = !hasTrophy(victim, stack);
            TrophyCollectionComponent collection;
            if(stack.has(SBComponents.TROPHY_COLECTION)){
                collection = stack.get(SBComponents.TROPHY_COLECTION).withTrophyAdded(victim.getType());
            }
            else{
                collection = TrophyCollectionComponent.ofTrophy(victim.getType());
            }
            stack.set(SBComponents.TROPHY_COLECTION,collection);
            if (killer instanceof Player killerPlayer) {
                if (newTrophy) {
                    String message = stack.getHoverName().getString()
                            + " acquired a "
                            + Component.translatable(victim.getType().toString()).getString()
                            + " trophy";
                    killerPlayer.displayClientMessage(Component.literal(message)
                            , true);
                }
                else {
                    int newValue = collection.getTrophyCopies(victim.getType());
                    if((isRanged && getRangedEntityDamageMultiple(newValue - 1) < (getRangedEntityDamageMultiple(newValue)))
                    || (!isRanged && getEntityDamageBonus(newValue - 1) < (getEntityDamageBonus(newValue))))
                    {
                        String message = stack.getHoverName().getString() + "'s "
                                + Component.translatable(victim.getType().toString()).getString()
                                + " trophy improved";
                        killerPlayer.displayClientMessage(Component.literal(message),true);
                    }
                }
            }
        }
    }

    private int getUniqueTrophyCount(ItemStack stack){
        if(stack.has(SBComponents.TROPHY_COLECTION)){
            return stack.get(SBComponents.TROPHY_COLECTION).trophies().size();
        }
        return 0;
    }

    private int getEntityTrophyCount(LivingEntity victim, ItemStack stack){
        if(stack.has(SBComponents.TROPHY_COLECTION)){
            return stack.get(SBComponents.TROPHY_COLECTION).getTrophyCopies(victim.getType());
        }
        return 0;
    }

    public List<TrophyCollectionComponent.Entry> getOrderedTrophyList(ItemStack stack){
        if(!stack.has(SBComponents.TROPHY_COLECTION)){
            return List.of();
        }
        List<TrophyCollectionComponent.Entry> listToOrder = new ArrayList<>(stack.get(SBComponents.TROPHY_COLECTION).trophies());
        listToOrder.sort(Comparator.comparingInt(TrophyCollectionComponent.Entry::count));
        return listToOrder;
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
