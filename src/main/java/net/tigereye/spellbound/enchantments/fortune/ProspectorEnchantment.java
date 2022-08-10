package net.tigereye.spellbound.enchantments.fortune;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.data.ProspectorManager;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.NextTickAction;
import net.tigereye.spellbound.interfaces.SpellboundExplosion;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

import java.util.List;
import java.util.Map;

public class ProspectorEnchantment extends SBEnchantment {
    //TODO: implement Prospector
    //TODO: create Prospector loot jsons and manager

    public static final String PROSPECTOR_LIST_KEY = Spellbound.MODID+"Prospector_List";

    public ProspectorEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.PROSPECTOR_RARITY), EnchantmentTarget.DIGGER, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.PROSPECTOR_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        return 15 + (level - 1) * 9;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 3;
        else return 0;
    }

    @Override
    public void onBreakBlock(int level, ItemStack stack, World world, BlockPos pos, BlockState state, PlayerEntity player) {
        //TODO: Populate and check the anti-abuse list
        if(world.isClient()){
            return;
        }
        if(state.getBlock().getHardness() == 0){
            return;
        }
        Map<Identifier,Float> rates = ProspectorManager.getDropRateMapWithBonuses(world,stack,pos,Spellbound.config.PROSPECTOR_RADIUS);
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
                    ((SpellboundLivingEntity)player).addNextTickAction(new ProspectorAction(world, pos, new ItemStack(treasure,count)));
                }
                else{
                    Spellbound.LOGGER.error(player.getName().getString() + "'s Prospector is looking for " + entry.getKey() + ", but cannot find it in the item registry!");
                }
            }
        }
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    private class ProspectorAction implements NextTickAction{

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
            world.spawnEntity(new ItemEntity(world,pos.getX(),pos.getY(),pos.getZ(),stack));
        }
    }
}
