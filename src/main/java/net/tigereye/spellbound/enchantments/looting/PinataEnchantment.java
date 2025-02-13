package net.tigereye.spellbound.enchantments.looting;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.NextTickAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PinataEnchantment extends SBEnchantment{
    private static final String PINATA_KILL_COUNT_KEY = Spellbound.MODID+"PinataCounter";
    
    public PinataEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.pinata.RARITY), SBEnchantmentTargets.ANY_WEAPON, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {return Spellbound.config.pinata.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.pinata.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.pinata.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.pinata.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.pinata.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.pinata.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.pinata.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.pinata.IS_FOR_SALE;}

    @Override
    public int getLootingValue(int level, LivingEntity user, ItemStack stack) {
        int killCount = getKillcount(stack);
        int looting = 0;
        if(killCount % Spellbound.config.pinata.KILLS_TO_PAYOUT == 0){
            looting = level*Spellbound.config.pinata.PAYOUT_MULTIPLIER;
        }
        return looting;
    }

    //note that onKill happens after getLootingValue
    @Override
    public void onKill(int level, ItemStack stack, DamageSource source, LivingEntity killer, LivingEntity victim){
        int killCount = getKillcount(stack);
        if(killCount % Spellbound.config.pinata.KILLS_TO_PAYOUT == 0){
            List<ItemEntity> items = killer.getWorld().getEntitiesByClass(ItemEntity.class, victim.getBoundingBox(), Objects::nonNull);
            ItemEntity nuggets = new ItemEntity(victim.getWorld(),victim.getX(),victim.getY(),victim.getZ(),new ItemStack(Items.GOLD_NUGGET,Spellbound.config.pinata.KILLS_TO_PAYOUT/4));
            killer.getWorld().spawnEntity(nuggets);
            items.add(nuggets);
            for (ItemEntity itemEntity:
                    items) {
                itemEntity.setPickupDelay(80);
            }
            if(killer instanceof SpellboundLivingEntity slEntity){
                slEntity.spellbound$addNextTickAction(new PinataLootFountainAction(items,killer.getRandom(),slEntity));
            }
        }
        setKillcount(stack,killCount+1);
    }

    @Override
    public List<Text> addTooltip(int level, ItemStack stack, PlayerEntity player, TooltipContext context) {
        List<Text> output = new ArrayList<>();
        int kills = getKillcount(stack);
        output.add(Text.translatable("enchantment.spellbound.pinata.tooltip",Spellbound.config.pinata.KILLS_TO_PAYOUT - ((kills-1)%Spellbound.config.pinata.KILLS_TO_PAYOUT)));
        return output;
    }

    private static int getKillcount(ItemStack item){
        NbtCompound nbtCompound = item.getOrCreateNbt();
        return nbtCompound.getInt(PINATA_KILL_COUNT_KEY);
    }

    private static void setKillcount(ItemStack item, int killCount){
        NbtCompound nbtCompound = item.getOrCreateNbt();
        nbtCompound.putLong(PINATA_KILL_COUNT_KEY,killCount);
    }

    private static class PinataLootFountainAction implements NextTickAction {

        List<ItemEntity> items;
        Random random;
        SpellboundLivingEntity owner; //I wonder if this will cause a memory leak... if it does, I'll need to null owner after creating the next action

        PinataLootFountainAction(List<ItemEntity> items, Random random, SpellboundLivingEntity owner){
            this.items = items;
            this.random = random;
            this.owner = owner;
        }

        @Override
        public void act() {
            if(!items.isEmpty()) {
                ItemEntity item = items.get(random.nextInt(items.size()));
                if (!item.isRemoved()) {
                    if (item.getStack().getCount() > 1) {
                        item.getStack().decrement(1);
                        ItemEntity itemCopy = item.copy();
                        itemCopy.getWorld().spawnEntity(itemCopy);
                        itemCopy.getStack().setCount(1);
                        itemCopy.setPickupDelay(5);
                        throwItem(itemCopy);
                    } else {
                        throwItem(item);
                        item.setPickupDelay(5);
                        items.remove(item);
                    }
                } else {
                    items.remove(item);
                }
                if (!items.isEmpty()) {
                    owner.spellbound$addNextTickAction(new PinataLootFountainAction(items, random, owner));
                }
            }
        }

        private void throwItem(ItemEntity item){
            item.setVelocity((random.nextDouble()-.5D)*0.2D, random.nextDouble()*0.5D,(random.nextDouble()-.5D)*0.2D);
            item.velocityDirty = true;
            item.velocityModified = true;
        }
    }
}