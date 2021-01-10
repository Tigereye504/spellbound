package net.tigereye.spellbound.enchantments;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Vec3d;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.*;

public class TrophyCollectorEnchantment extends SBEnchantment implements CustomConditionsEnchantment{

    private static final String TROPHY_COLLECTOR_KEY = Spellbound.MODID+"TrophyCollector";
    private static final String UNIQUE_TROPHY_COUNT_KEY = Spellbound.MODID+"UniqueTrophyCount";
    public TrophyCollectorEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
    }

    public int getMinPower(int level) {
        return 1;
    }

    public int getMaxPower(int level) {
        return 50;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        float UniqueTrophyDamage = (float)Math.sqrt(getUniqueTrophyCount(stack))/2;
        int EntityTrophyDamage = 0;
        if(defender instanceof LivingEntity) {
            EntityTrophyDamage = (int) (Math.sqrt(getEntityTrophyCount((LivingEntity)defender, stack)) / 4);
        }
        return UniqueTrophyDamage + EntityTrophyDamage;
    }

    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        float UniqueTrophyDamage = (float)Math.sqrt(getUniqueTrophyCount(stack))/2;
        int EntityTrophyDamage = 0;
        if(defender instanceof LivingEntity) {
            EntityTrophyDamage = (int) (Math.sqrt(getEntityTrophyCount((LivingEntity)defender, stack)) / 4);
        }
        return damage + ((UniqueTrophyDamage + EntityTrophyDamage)*2);
    }

    public void onKill(int level, ItemStack stack, LivingEntity killer, LivingEntity victim){
        addTrophy(victim,killer,stack);
    }

    public void onActivate(int level, PlayerEntity player, ItemStack stack, Entity target) {
        if(!player.world.isClient && player.getPose() == EntityPose.CROUCHING){
            CompoundTag tag = stack.getOrCreateSubTag(TROPHY_COLLECTOR_KEY);
            Set<String> keys = tag.getKeys();

            player.sendMessage(new LiteralText(""),false);
            player.sendMessage(new LiteralText("----------------------------"),false);
            keys.forEach((trophyKey) -> {
                if(!trophyKey.equals(UNIQUE_TROPHY_COUNT_KEY)) {
                    player.sendMessage(new LiteralText(
                            tag.getInt(trophyKey) + " ")
                                    .append(new TranslatableText(trophyKey))
                                    .append(" (+"+(int)(Math.sqrt(tag.getInt(trophyKey))/4)+")")
                    , false);
                }
            });

            player.sendMessage(new LiteralText(
                    "--" + tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + " Unique Trophies (+"+String.format("%.1f", Math.sqrt(getUniqueTrophyCount(stack))/2)+")--"
            ), false);
        }
    }

    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context) {
        List<Text> output = new ArrayList<Text>();
        CompoundTag tag = stack.getOrCreateSubTag(TROPHY_COLLECTOR_KEY);
        Set<String> keys = tag.getKeys();
        Map<String,Integer> keyIntMap = new HashMap<String,Integer>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_TROPHY_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        output.add(new LiteralText(
                "--" + tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + " Unique Trophies (+"
                        +String.format("%.1f", Math.sqrt(getUniqueTrophyCount(stack))/2)+")--"));
        keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
                .forEach((entry) -> {
                    output.add(new LiteralText(
                            entry.getValue() + " ")
                            .append(new TranslatableText(entry.getKey()))
                            .append(" (+" + (int) (Math.sqrt(entry.getValue()) / 4) + ")"));
                });
        output.add(new LiteralText("----------------------------"));
        /*
        output.add(new LiteralText(
                "--" + tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + " Unique Trophies (+"
                        +String.format("%.1f", Math.sqrt(getUniqueTrophyCount(stack))/2)+")--"));
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_TROPHY_COUNT_KEY)) {
                output.add(new LiteralText(
                                tag.getInt(trophyKey) + " ")
                                .append(new TranslatableText(trophyKey))
                                .append(" (+"+(int)(Math.sqrt(tag.getInt(trophyKey))/4)+")"));
            }
        });
        output.add(new LiteralText("----------------------------"));
        */
        return output;
    }

    public boolean isTreasure() {
        return false;
    }

    //I want to disallow damageEnchantments and anything else that disallows damageEnchantments
    //as typically the later is trying to be another form of damage enchantment
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.SHARPNESS);
    }

    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                //|| stack.getItem() instanceof BowItem
                //|| stack.getItem() instanceof CrossbowItem
                //TODO: implement getArrowDamage
                || stack.getItem() == Items.BOOK;
    }

    private boolean hasTrophy(LivingEntity victim, ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(TROPHY_COLLECTOR_KEY);
        if(tag.contains(victim.getType().toString())){
            return true;
        }
        return false;
    }

    private boolean addTrophy(LivingEntity victim, LivingEntity killer, ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(TROPHY_COLLECTOR_KEY);
        if(!hasTrophy(victim,stack)){
            tag.putInt(UNIQUE_TROPHY_COUNT_KEY,tag.getInt(UNIQUE_TROPHY_COUNT_KEY)+1);
            tag.putInt(victim.getType().toString(),1);
            if(killer instanceof PlayerEntity){
                String message = stack.getName().getString()
                        + " acquired a "
                        + new TranslatableText(victim.getType().toString()).getString()
                        + " trophy";
                ((PlayerEntity)killer).sendMessage(new LiteralText(message)
                        , true);
            }
            return true;
        }
        else{
            int newValue = tag.getInt(victim.getType().toString())+1;
            tag.putInt(victim.getType().toString(),newValue);
            if(((int) (Math.sqrt(newValue-1) / 4)) < ((int) (Math.sqrt(newValue) / 4))){
                String message = stack.getName().getString()
                        + "'s "
                        + new TranslatableText(victim.getType().toString()).getString()
                        + " trophy improved";
                ((PlayerEntity)killer).sendMessage(new LiteralText(message)
                        , true);
            }
            return false;
        }
    }

    private int getUniqueTrophyCount(ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(TROPHY_COLLECTOR_KEY);
        return tag.getInt(UNIQUE_TROPHY_COUNT_KEY);
    }

    private int getEntityTrophyCount(LivingEntity victim, ItemStack stack){
        CompoundTag tag = stack.getOrCreateSubTag(TROPHY_COLLECTOR_KEY);
        return tag.getInt(victim.getType().toString());
    }
}
