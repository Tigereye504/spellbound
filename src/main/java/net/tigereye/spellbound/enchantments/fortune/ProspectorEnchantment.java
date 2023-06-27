package net.tigereye.spellbound.enchantments.fortune;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.ProspectorManager;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.NextTickAction;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.Map;

public class ProspectorEnchantment extends SBEnchantment {

    public static final String PROSPECTOR_LIST_KEY = Spellbound.MODID+"Prospector_List";

    public ProspectorEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.prospector.RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND},true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.prospector.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.prospector.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.prospector.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.prospector.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.prospector.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.prospector.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.prospector.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.prospector.IS_FOR_SALE;}
    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //TODO: Populate and check the anti-abuse list
        if(world.isClient()){
            return;
        }
        if(state.getBlock().getHardness() == 0){
            return;
        }
        if(detectAbuse(stack, world, pos)){
            return;
        }
        Map<Identifier,Float> rates = ProspectorManager.getDropRateMapWithBonuses(world,stack,pos,Spellbound.config.prospector.RADIUS);
        Random random = player.getRandom();
        for (Map.Entry<Identifier,Float> entry: rates.entrySet()){
            if(entry.getValue() > 0) {
                Item treasure = Registry.ITEM.get(entry.getKey());
                if (treasure != Items.AIR) {
                    if(Spellbound.DEBUG){
                        Spellbound.LOGGER.info("Prospecting "+ Text.translatable(treasure.getTranslationKey()).getString() + ". Attempts: "+level+". Odds: " + entry.getValue());
                    }
                    int count = 0;
                    for (int i = 0; i < level; i++) {
                        if (random.nextFloat() < entry.getValue()){
                            count++;
                        }
                    }
                    ((SpellboundLivingEntity)player).addNextTickAction(new ProspectorAction(world, pos, new ItemStack(treasure, count)));
                }
                else{
                    Spellbound.LOGGER.error(player.getName().getString() + "'s Prospector is looking for " + entry.getKey() + ", but cannot find it in the item registry!");
                }
            }
        }
    }

    private boolean detectAbuse(ItemStack stack, World world, BlockPos pos){
        NbtCompound tag = stack.getOrCreateSubNbt(PROSPECTOR_LIST_KEY);

        long lastAccessTime = tag.getLong("lastAccessTime");
        if(world.getTime() - lastAccessTime > Spellbound.config.prospector.ABUSE_MEMORY){
            tag = new NbtCompound();
            stack.setSubNbt(PROSPECTOR_LIST_KEY,tag);
        }
        tag.putLong("lastAccessTime",world.getTime());

        String encodedPosition = pos.getX()+"_"+pos.getY()+"_"+pos.getZ();
        int samePosDigs = tag.getInt(encodedPosition);
        if(samePosDigs > Spellbound.config.prospector.ABUSE_THRESHOLD){
            return true;
        }
        else{
            tag.putInt(encodedPosition,samePosDigs+1);
            return false;
        }
    }

    private static class ProspectorAction implements NextTickAction{

        World world;
        BlockPos pos;
        ItemStack stack;

        ProspectorAction(World world, BlockPos pos, ItemStack stack){
            this.world = world;
            this.pos = pos;
            this.stack = stack;
        }
        @Override
        public void act() {
            world.spawnEntity(new ItemEntity(world,pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5,stack));
        }
    }
}
