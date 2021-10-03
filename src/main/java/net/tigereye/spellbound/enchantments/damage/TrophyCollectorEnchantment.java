package net.tigereye.spellbound.enchantments.damage;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;

import java.util.*;

public class TrophyCollectorEnchantment extends SBEnchantment implements CustomConditionsEnchantment {

    private static final String TROPHY_COLLECTOR_KEY = Spellbound.MODID+"TrophyCollector";
    private static final String UNIQUE_TROPHY_COUNT_KEY = Spellbound.MODID+"UniqueTrophyCount";

    public TrophyCollectorEnchantment() {
        super(Rarity.VERY_RARE, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.TROPHY_COLLECTOR_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        return 1;
    }

    @Override
    public int getMaxPower(int level) {
        return 50;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 1;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

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
    public void onActivate(int level, PlayerEntity player, ItemStack stack, Entity target) {
        if(!player.world.isClient && player.getPose() == EntityPose.CROUCHING){
            NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
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

    @Override
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context) {
        boolean isRanged = stack.getItem() instanceof RangedWeaponItem;
        List<Text> output = new ArrayList<>();
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        Set<String> keys = tag.getKeys();
        Map<String,Integer> keyIntMap = new HashMap<>();
        keys.forEach((trophyKey) -> {
            if(!trophyKey.equals(UNIQUE_TROPHY_COUNT_KEY)) {
                keyIntMap.put(trophyKey,tag.getInt(trophyKey));
            }
        });
        if(isRanged) {
            output.add(new LiteralText(
                    "--" + getUniqueTrophyCount(stack) + " Unique Trophies (+"
                            + String.format("%.2f", getRangedUniqueDamageMultiple(getUniqueTrophyCount(stack))) + "x)--"));
        }
        else{
            output.add(new LiteralText(
                    "--" + tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + " Unique Trophies (+"
                            + String.format("%.1f", getUniqueDamageBonus(getUniqueTrophyCount(stack))) + ")--"));
        }
        keyIntMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(10)
                .forEach((entry) -> {
                    if(isRanged) {
                        output.add(new LiteralText(
                                entry.getValue() + " ")
                                .append(new TranslatableText(entry.getKey()))
                                .append(" (+" + String.format("%.1f", getRangedEntityDamageMultiple(entry.getValue())) + "x)"));
                    }
                    else{
                        output.add(new LiteralText(
                                entry.getValue() + " ")
                                .append(new TranslatableText(entry.getKey()))
                                .append(" (+" + getEntityDamageBonus(entry.getValue()) + ")"));
                    }
                });
        output.add(new LiteralText("--------------------------"));
        return output;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    //I want to disallow damageEnchantments and anything else that disallows damageEnchantments
    //as typically the later is trying to be another form of damage enchantment
    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other)
                && other.canCombine(Enchantments.SHARPNESS)
                && other.canCombine(Enchantments.POWER);
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof SwordItem
                || stack.getItem() instanceof AxeItem
                || stack.getItem() instanceof TridentItem
                || stack.getItem() instanceof RangedWeaponItem
                || stack.getItem() == Items.BOOK;
    }

    private boolean hasTrophy(LivingEntity victim, ItemStack stack){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        return tag.contains(victim.getType().toString());
    }

    private boolean addTrophy(LivingEntity victim, LivingEntity killer, ItemStack stack,boolean isRanged){
        NbtCompound tag = stack.getOrCreateSubNbt(TROPHY_COLLECTOR_KEY);
        if(!(victim instanceof PassiveEntity || victim instanceof WaterCreatureEntity) || victim instanceof Angerable || victim instanceof Monster) {
            if (!hasTrophy(victim, stack)) {
                tag.putInt(UNIQUE_TROPHY_COUNT_KEY, tag.getInt(UNIQUE_TROPHY_COUNT_KEY) + 1);
                tag.putInt(victim.getType().toString(), 1);
                if (killer instanceof PlayerEntity) {
                    String message = stack.getName().getString()
                            + " acquired a "
                            + new TranslatableText(victim.getType().toString()).getString()
                            + " trophy";
                    ((PlayerEntity) killer).sendMessage(new LiteralText(message)
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
                                    + new TranslatableText(victim.getType().toString()).getString()
                                    + " trophy improved";
                            ((PlayerEntity) killer).sendMessage(new LiteralText(message)
                                    , true);
                        }
                    } else {
                        if (getEntityDamageBonus(newValue - 1) < (getEntityDamageBonus(newValue))) {
                            String message = stack.getName().getString()
                                    + "'s "
                                    + new TranslatableText(victim.getType().toString()).getString()
                                    + " trophy improved";
                            ((PlayerEntity) killer).sendMessage(new LiteralText(message)
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

    private float getUniqueDamageBonus(int uniques){
        return (float)Math.sqrt(uniques)/2;
    }

    private int getEntityDamageBonus(int kills){
        return (int) (Math.sqrt(kills) / 4);
    }

    private float getRangedUniqueDamageMultiple(int uniques){
        return (float)Math.sqrt(uniques)*0.2f;
    }

    private float getRangedEntityDamageMultiple(int kills){
        return ((int)(Math.sqrt(kills)/4))*0.2f;
    }
}
