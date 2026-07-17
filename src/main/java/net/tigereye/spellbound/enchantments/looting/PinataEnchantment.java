package net.tigereye.spellbound.enchantments.looting;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.DelayedAction;
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
    public boolean isTreasureOnly() {return Spellbound.config.pinata.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.pinata.IS_FOR_SALE;}

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
            List<ItemEntity> items = killer.level().getEntitiesOfClass(ItemEntity.class, victim.getBoundingBox(), Objects::nonNull);
            if(items.isEmpty()){
                return; //just end it here, and let the fountain trigger on something with actual drops.
            }
            for (ItemEntity itemEntity:
                    items) {
                itemEntity.setPickUpDelay(80);
            }
            if(killer instanceof SpellboundLivingEntity slEntity){
                slEntity.spellbound$addDelayedAction(new PinataLootFountainAction(items,killer.getRandom(),slEntity));
            }
        }
        killCount++;
        setKillcount(stack,killCount);
        int killsToPayout = Spellbound.config.pinata.KILLS_TO_PAYOUT - ((killCount-1)%Spellbound.config.pinata.KILLS_TO_PAYOUT);
        if(killer instanceof Player playerEntity && killsToPayout <= Spellbound.config.pinata.ADVANCE_NOTICE && killsToPayout != 0){
            if(killsToPayout == 1){
                playerEntity.displayClientMessage(Component.translatable("enchantment.spellbound.pinata.message.nextKill"), true);
            }
            else {
                playerEntity.displayClientMessage(Component.translatable("enchantment.spellbound.pinata.message.countdown", killsToPayout), true);
            }
        }
    }

    @Override
    public List<Component> addTooltip(int level, ItemStack stack, Player player, TooltipFlag context) {
        List<Component> output = new ArrayList<>();
        int kills = getKillcount(stack);
        output.add(Component.translatable("enchantment.spellbound.pinata.tooltip",Spellbound.config.pinata.KILLS_TO_PAYOUT - ((kills-1)%Spellbound.config.pinata.KILLS_TO_PAYOUT)));
        return output;
    }

    private static int getKillcount(ItemStack item){
        CompoundTag nbtCompound = item.getOrCreateTag();
        return nbtCompound.getInt(PINATA_KILL_COUNT_KEY);
    }

    private static void setKillcount(ItemStack item, int killCount){
        CompoundTag nbtCompound = item.getOrCreateTag();
        nbtCompound.putLong(PINATA_KILL_COUNT_KEY,killCount);
    }

    private static class PinataLootFountainAction extends DelayedAction {

        List<ItemEntity> items;
        RandomSource random;
        SpellboundLivingEntity owner;

        PinataLootFountainAction(List<ItemEntity> items, RandomSource random, SpellboundLivingEntity owner){
            this.items = items;
            this.random = random;
            this.owner = owner;
        }

        @Override
        public void act() {
            if(!items.isEmpty()) {
                ItemEntity item = items.get(random.nextInt(items.size()));
                if (!item.isRemoved()) {
                    if (item.getItem().getCount() > 1) {
                        item.getItem().shrink(1);
                        ItemEntity itemCopy = item.copy();
                        itemCopy.level().addFreshEntity(itemCopy);
                        itemCopy.getItem().setCount(1);
                        itemCopy.setPickUpDelay(5);
                        throwItem(itemCopy);
                    } else {
                        throwItem(item);
                        item.setPickUpDelay(5);
                        items.remove(item);
                    }
                } else {
                    items.remove(item);
                }
                if (!items.isEmpty()) {
                    owner.spellbound$addDelayedAction(new PinataLootFountainAction(items, random, owner));
                }
            }
        }

        private void throwItem(ItemEntity item){
            item.setDeltaMovement((random.nextDouble()-.5D)*0.2D, random.nextDouble()*0.5D,(random.nextDouble()-.5D)*0.2D);
            item.hasImpulse = true;
            item.hurtMarked = true;
        }
    }
}