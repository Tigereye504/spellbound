package net.tigereye.spellbound.enchantments.meta;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class StoriedEnchantment extends SBEnchantment {
    public static final String STORIED_XP_KEY = Spellbound.MODID+":storied_xp";

    public StoriedEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.storied.RARITY), //enchantment weight
            Spellbound.config.storied.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.storied.BASE_POWER,Spellbound.config.storied.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.storied.BASE_POWER+Spellbound.config.storied.POWER_RANGE,Spellbound.config.storied.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.storied.RARITY-1), //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            false); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.storied.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.storied.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.storied.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.storied.IS_FOR_SALE;}
    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }
    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        Level world = killer.level();
        if(!world.isClientSide()){
            gainStoryXP(stack,killer,victim.getExperienceReward());
        }
    }
    @Override
    public void onBreakBlock(int level, ItemStack stack, Level world, BlockPos pos, BlockState state, Player player) {
        if(!world.isClientSide()){
            gainStoryXP(stack,player,state.getBlock().defaultDestroyTime()*0.66F);
        }
    }

    @Override
    public List<Component> addTooltip(int level, ItemStack stack, Player player, TooltipFlag context) {
        List<Component> output = new ArrayList<>();
        int chapter = getStoryChapter(stack);
        output.add(Component.literal(
                "Chapter "+ chapter +": "+((int)stack.getOrDefault(SBComponents.STORIED_XP,0).floatValue())+"/"
                        +getXPToNextChapter(chapter)));
        return output;
    }

    private void gainStoryXP(ItemStack stack, LivingEntity owner, float xp){
        float oldXp = stack.getOrDefault(SBComponents.STORIED_XP, 0).floatValue();
        if(oldXp == 0){
            beginStory(owner,stack,EnchantmentHelper.getEnchantmentsForCrafting(stack));
        }
        int currentChapter = getStoryChapter(stack);
        float newXP = xp + oldXp;
        double xpToNextChapter = getXPToNextChapter(currentChapter);
        while(newXP > xpToNextChapter){
            newXP -= (float) xpToNextChapter;
            ++currentChapter;
            xpToNextChapter = getXPToNextChapter(currentChapter);
            advanceStory(owner,stack,stack.getEnchantments());
        }
        stack.set(SBComponents.STORIED_XP, newXP);
    }

    private void beginStory(LivingEntity entity, ItemStack stack, ItemEnchantments enchantments){

        //TODO: Get one of those rare item name generators, use it if the item lacks a custom name.

        //Check if at least one other enchantment exists. If not, roll up a new enchantment to get started.
        String message = stack.getHoverName().getString()+ "'s story begins.";
        if(getLevelableEnchantments(enchantments).isEmpty()) {
            Enchantment selection = selectRandomAddableEnchantment(entity,stack,false);
            if(selection != null){
                message += " Gained "+ selection.getFullname(1).getString() +"!";
                ((SpellboundLivingEntity)entity).spellbound$addDelayedAction(new StoriedSetEnchantmentLevelAction(stack, selection, 1));
            }
            else{
                message = stack.getHoverName().getString()+ "has no story to tell.";
                ((SpellboundLivingEntity)entity).spellbound$addDelayedAction(new StoriedSetEnchantmentLevelAction(stack, SBEnchantments.STORIED, 0));
            }
        }
        //Finally, tell the player the story has begun.
        if(entity instanceof ServerPlayer pEntity) {
            pEntity.displayClientMessage(Component.literal(message), true);
        }
    }

    private int getXPToNextChapter(int level){
        return (int)((Spellbound.config.storied.WORDS_PER_LEVEL_BASE
                +(Spellbound.config.storied.WORDS_PER_LEVEL_FIRST_DEGREE*(level-1)
                +Spellbound.config.storied.WORDS_PER_LEVEL_SECOND_DEGREE *Math.pow(level-1,2))));
    }

    private int getStoryChapter(ItemStack stack){
        AtomicInteger chapter = new AtomicInteger(-1);
        SBEnchantmentHelper.runIterationOnItem((enchantment, level) -> chapter.addAndGet(level),stack);
        return chapter.get();
    }
    private void advanceStory(LivingEntity entity, ItemStack stack, ItemEnchantments enchantments){
        //Determine which enchantments are not at max level
        List<Tuple<Enchantment,Integer>> levelableEnchantments = getLevelableEnchantments(enchantments);
        //if there are no level-able enchantments, try to add a new enchantment. Otherwise, the item's story is finished.
        Enchantment selection = null;
        int level = 1;
        if(levelableEnchantments.isEmpty() && Spellbound.config.storied.ALLOW_NEW_PLOTHOOKS){
            selection = selectRandomAddableEnchantment(entity,stack,false);
        }
        //otherwise, choose a non-maxed enchantment at random to level up
        else{
            if(Spellbound.config.storied.ALLOW_NEW_PLOTHOOKS){
                int targetNumber = levelableEnchantments.size()*Spellbound.config.storied.RARITY_OF_NEW_PLOTHOOKS;
                int random = entity.getRandom().nextInt(targetNumber+1);
                if(random == targetNumber){
                    selection = selectRandomAddableEnchantment(entity,stack,false);
                }
            }
            if(selection == null) {
                Tuple<Enchantment, Integer> existingEnchantment = levelableEnchantments.get(entity.getRandom().nextInt(levelableEnchantments.size()));
                selection = existingEnchantment.getA();
                level = existingEnchantment.getB()+1;
            }
        }

        //apply the upgrade or remove storied
        if(selection != null){
            ((SpellboundLivingEntity)entity).spellbound$addDelayedAction(new StoriedSetEnchantmentLevelAction(stack,selection,level));
            String message = stack.getHoverName().getString() + "'s story continues. Gained "+ selection.getFullname(level).getString() +"!";
            if(entity instanceof ServerPlayer pEntity) {
                pEntity.displayClientMessage(Component.literal(message), true);
            }
        }
        else{
            ((SpellboundLivingEntity) entity).spellbound$addDelayedAction(new StoriedSetEnchantmentLevelAction(stack, SBEnchantments.STORIED, 0));
            String message = stack.getHoverName().getString() + "'s story is complete.";
            if(entity instanceof ServerPlayer pEntity) {
                pEntity.displayClientMessage(Component.literal(message), true);
            }
        }
    }

    private List<Tuple<Enchantment,Integer>> getLevelableEnchantments(ItemEnchantments enchantments){
        List<Tuple<Enchantment,Integer>> levelableEnchantments = new ArrayList<>();
        enchantments.entrySet().forEach((enchantmentHolder) -> {
            if(enchantmentHolder.getKey().value().getMaxLevel() > enchantmentHolder.getIntValue()) {
                levelableEnchantments.add(new Tuple<>(enchantmentHolder.getKey().value(), enchantmentHolder.getIntValue()));
            }
        });
        return levelableEnchantments;
    }

    private Enchantment selectRandomAddableEnchantment(LivingEntity entity, ItemStack stack, boolean mustBeLevelable){

        List<Enchantment> options = new LinkedList<>();
        Set<Holder<Enchantment>> existingEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet();
        for (Enchantment enchantment : BuiltInRegistries.ENCHANTMENT) {
            if ((enchantment.getMaxLevel() != 1 || !mustBeLevelable)
                    && enchantment.getMaxLevel() > 0 //to prevent disabled enchantments from being rolled
                    && ((!enchantment.isCurse()) || Spellbound.config.storied.CAN_CREATE_CURSE)
                    && ((!enchantment.isTreasureOnly()) || Spellbound.config.storied.CAN_CREATE_TREASURE)
                    && ((enchantment.isDiscoverable()) || Spellbound.config.storied.CAN_CREATE_NON_LOOT)
                    && enchantment.canEnchant(stack))
            {
                if(EnchantmentHelper.isEnchantmentCompatible(existingEnchantments, enchantment)) {
                    options.add(enchantment);
                }
            }
        }

        if(!options.isEmpty()){
            return options.get(entity.getRandom().nextInt(options.size()));
        }
        return null;

        /*
        List<EnchantmentLevelEntry> levelEntries = EnchantmentHelper.getPossibleEntries(50, stack, Spellbound.config.storied.CAN_CREATE_TREASURE);
        EnchantmentLevelEntry selection = null;
        while (selection == null && !levelEntries.isEmpty()) {
            selection = levelEntries.get(entity.getRandom().nextInt(levelEntries.size()));
            //remove if the selection is the wrong level, isn't levelable, or is incompatible with the chosen item
            if (selection.level > 1
                || (mustBeLevelable && selection.enchantment.getMaxLevel() == 1)
                || (selection.enchantment.isCursed() && !Spellbound.config.storied.CAN_CREATE_CURSE)
                || !selection.enchantment.isAcceptableItem(stack))
            {
                levelEntries.remove(selection);
                selection = null;
            }
            //remove if the section is incompatible with any existing enchantments
            if(selection != null){
                for (Enchantment enchantment:
                     EnchantmentHelper.get(stack).keySet()) {
                    if(!selection.enchantment.canCombine(enchantment)){
                        levelEntries.remove(selection);
                        selection = null;
                        break;
                    }
                }
            }
        }
        return selection != null ? selection.enchantment : null;
        */
    }
    private static class StoriedSetEnchantmentLevelAction extends DelayedAction {

        ItemStack stack;
        Enchantment enchantment;
        int newLevel;

        StoriedSetEnchantmentLevelAction(ItemStack stack, Enchantment enchantment, int newLevel){
            this.stack = stack;
            this.enchantment = enchantment;
            this.newLevel = newLevel;
        }
        @Override
        public void act() {
            ItemEnchantments.Mutable enchantmentMutable = new ItemEnchantments.Mutable(stack.getEnchantments());
            if(newLevel == 0){
                enchantmentMutable.removeIf((entry) -> entry.value() == enchantment);
            }
            else {
                enchantmentMutable.set(enchantment, newLevel);
            }
            EnchantmentHelper.setEnchantments(stack, enchantmentMutable.toImmutable());
        }
    }
}
