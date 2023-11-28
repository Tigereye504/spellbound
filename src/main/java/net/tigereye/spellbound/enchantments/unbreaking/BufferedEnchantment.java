package net.tigereye.spellbound.enchantments.unbreaking;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

public class BufferedEnchantment extends SBEnchantment {

    private static final String BUFFER_TIME_KEY = Spellbound.MODID+"BufferTime";
    private static final int BUFFER_COLOR = 0x1cd8e3;
    private static final int BUFFER_DULL_COLOR = 0x579ca2;

    public BufferedEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.buffered.RARITY), EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND},false);
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.buffered.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.buffered.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.buffered.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.buffered.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.buffered.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.buffered.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.buffered.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.buffered.IS_FOR_SALE;}

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayerEntity entity, int loss){
        if(entity == null){
            return loss;
        }
        World world = entity.getWorld();
        if(!world.isClient()){
            float durabilityBuffer = getDurabilityBuffer(level, stack, entity.getWorld());
            if(Spellbound.DEBUG) {
                Spellbound.LOGGER.info(stack.getName().getString() + " has " + durabilityBuffer + " buffer");
            }
            if(durabilityBuffer >= 1){
                int cost = (int)Math.min(loss,Math.floor(durabilityBuffer));
                setDurabilityBuffer(level, stack, entity.getWorld(), durabilityBuffer-cost);
                if(Spellbound.DEBUG){
                    Spellbound.LOGGER.info("Buffered prevented "+cost+" durability loss");
                    Spellbound.LOGGER.info(durabilityBuffer-cost + " buffer remains");
                }
                return loss-cost;
            }
        }
        return loss;
    }

    private static float getDurabilityBuffer(int level, Long itemTime, Long currentTime){
        long timeDiff = currentTime - itemTime;
        return (float) (Math.min(Spellbound.config.buffered.MAX_PER_RANK,
                timeDiff / Spellbound.config.buffered.RECOVERY_RATE)
                * level);
    }

    private static float getDurabilityBuffer(int level, ItemStack item, World world){
        NbtCompound nbtCompound = item.getOrCreateNbt();
        Long time = nbtCompound.getLong(BUFFER_TIME_KEY);
        return getDurabilityBuffer(level, time, world.getTime());
    }

    private static float getDurabilityBuffer(ItemStack item, World world){
        return getDurabilityBuffer(EnchantmentHelper.getLevel(SBEnchantments.BUFFERED,item),item,world);
    }

    private static void setDurabilityBuffer(int level, ItemStack item, World world, float buffer){
        NbtCompound nbtCompound = item.getOrCreateNbt();
        long timeDiff = (long) (buffer * Spellbound.config.buffered.RECOVERY_RATE)/level;
        nbtCompound.putLong(BUFFER_TIME_KEY,world.getTime()-timeDiff);
    }

    @Environment(EnvType.CLIENT)
    public static void RenderBufferItemOverlay(DrawContext drawContext, ItemStack stack, int x, int y){
        World world = MinecraftClient.getInstance().world;
        int level = EnchantmentHelper.getLevel(SBEnchantments.BUFFERED,stack);
        if(world == null || level == 0){
            return;
        }
        float durabilityBuffer = BufferedEnchantment.getDurabilityBuffer(stack,world);

        switch (Spellbound.config.buffered.DISPLAY) {
            case "Aura", "aura" -> renderBufferAsAura(drawContext, durabilityBuffer, x, y);
            default -> renderBufferAsBar(drawContext, level, durabilityBuffer, x, y);
        }

    }

    @Environment(EnvType.CLIENT)
    private static void renderBufferAsAura(DrawContext drawContext, float durability, int x, int y){

        int totalLayers = (int)Math.ceil(durability);
        for(int i = 0; i < totalLayers && i < 8; i++){
            int color = durability >= 1 ? BUFFER_COLOR : BUFFER_DULL_COLOR;
            int alpha = 15 + ((totalLayers-i) * 10);
            if(durability < 1){
                alpha *= durability;
            }
            else{
                alpha += 10 * (durability%1);
            }
            int longSide = 15 - (i*2);

            drawContext.fill(RenderLayer.getGuiOverlay(), x + i, y + i, x + i + longSide, y + i + 1, color | alpha << 24);
            drawContext.fill(RenderLayer.getGuiOverlay(), x + 15 - i, y + i, x + 16 - i, y + i + longSide, color | alpha << 24);
            drawContext.fill(RenderLayer.getGuiOverlay(), x + i, y + 1 + i, x + i + 1, y + 1 + i + longSide, color | alpha << 24);
            drawContext.fill(RenderLayer.getGuiOverlay(), x + 1 + i, y + 15 - i, x + 1 + i + longSide, y + 16 - i, color | alpha << 24);
            durability--;
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static void renderBufferAsBar(DrawContext drawContext, int level, float durability, int x, int y){
        int color = durability >= 1 ? BUFFER_COLOR : BUFFER_DULL_COLOR;
        int alpha = 255;//durabilityBuffer >= 1 ? 255 : 100;
        int width = (int) Math.ceil(Math.min(13, 13 * durability / (level * (float) Spellbound.config.buffered.MAX_PER_RANK)));

        drawContext.fill(RenderLayer.getGuiOverlay(), x + 2, y + 14, x + 2 + width, y + 15, color | alpha << 24);
    }

}
