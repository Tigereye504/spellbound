package net.tigereye.spellbound.enchantments.unbreaking;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
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

public class BufferedEnchantment extends SBEnchantment {

    private static final String BUFFER_TIME_KEY = Spellbound.MODID+"BufferTime";
    private static final int BUFFER_COLOR = 0x1cd8e3;
    private static final int BUFFER_DULL_COLOR = 0x579ca2;

    public BufferedEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.BREAKABLE, new EquipmentSlot[] {EquipmentSlot.MAINHAND});
        REQUIRES_PREFERRED_SLOT = false;
    }

    @Override
    public int getMinPower(int level) {
        return 5 + (level - 1) * 8;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + 50;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.BUFFERED_ENABLED;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return 3;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayerEntity entity, int loss){
        if(entity == null){
            return loss;
        }
        World world = entity.world;
        if(!world.isClient()){
            float durabilityBuffer = getDurabilityBuffer(level, stack, entity.world);
            Spellbound.LOGGER.info(stack.getName().getString() +" has "+ durabilityBuffer + " buffer");
            if(durabilityBuffer >= 1){
                int cost = (int)Math.min(loss,Math.floor(durabilityBuffer));
                setDurabilityBuffer(level, stack, entity.world, durabilityBuffer-cost);
                if(Spellbound.DEBUG){
                    Spellbound.LOGGER.info("Buffered prevented "+cost+" durability loss");
                    Spellbound.LOGGER.info(durabilityBuffer-cost + " buffer remains");
                }
                return loss-cost;
            }
        }
        return loss;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    private static float getDurabilityBuffer(int level, Long itemTime, Long currentTime){
        long timeDiff = currentTime - itemTime;
        return (float) (Math.min(Spellbound.config.BUFFER_MAX_PER_RANK,
                timeDiff / Spellbound.config.BUFFER_RECOVERY_RATE)
                * level);
    }

    private static float getDurabilityBuffer(int level, ItemStack item, World world){
        NbtCompound nbtCompound = item.getOrCreateNbt();
        Long time = nbtCompound.getLong(BUFFER_TIME_KEY);
        return getDurabilityBuffer(level, time, world.getTime());
    }

    public static float getDurabilityBuffer(ItemStack item, World world){
        return getDurabilityBuffer(EnchantmentHelper.getLevel(SBEnchantments.BUFFERED,item),item,world);
    }

    private static void setDurabilityBuffer(int level, ItemStack item, World world, float buffer){
        NbtCompound nbtCompound = item.getOrCreateNbt();
        long timeDiff = (long) (buffer * Spellbound.config.BUFFER_RECOVERY_RATE)/level;
        nbtCompound.putLong(BUFFER_TIME_KEY,world.getTime()-timeDiff);
    }

    @Environment(EnvType.CLIENT)
    public static void RenderBufferItemOverlay(ItemStack stack, int x, int y){
        World world = MinecraftClient.getInstance().world;
        int level = EnchantmentHelper.getLevel(SBEnchantments.BUFFERED,stack);
        if(world == null || level == 0){
            return;
        }
        float durabilityBuffer = BufferedEnchantment.getDurabilityBuffer(stack,world);

        RenderSystem.disableDepthTest();
        //RenderSystem.disableTexture();
        //RenderSystem.disableBlend();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        switch (Spellbound.config.BUFFERED_DISPLAY){
            case "Aura":
            case "aura":
                renderBufferAsAura(level, durabilityBuffer, buffer, x, y);
                break;
            case "Bar":
            case "bar":
            default:
                renderBufferAsBar(level, durabilityBuffer, buffer, x, y);
                break;
        }

        //RenderSystem.enableBlend();
        //RenderSystem.enableTexture();
        RenderSystem.enableDepthTest();

    }

    @Environment(EnvType.CLIENT)
    private static void renderBufferAsAura(int level, float durability, BufferBuilder buffer, int x, int y){

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

            renderGuiQuad(buffer, x + i, y + i, longSide, 1, color >> 16 & 255, color >> 8 & 255, color & 255, alpha);
            renderGuiQuad(buffer, x + 15 - i, y + i, 1, longSide, color >> 16 & 255, color >> 8 & 255, color & 255, alpha);
            renderGuiQuad(buffer, x + i, y + 1 + i, 1, longSide, color >> 16 & 255, color >> 8 & 255, color & 255, alpha);
            renderGuiQuad(buffer, x + 1 + i, y + 15 - i, longSide, 1, color >> 16 & 255, color >> 8 & 255, color & 255, alpha);
            durability--;
        }
    }


    @Environment(EnvType.CLIENT)
    private static void renderBufferAsBar(int level, float durability, BufferBuilder buffer, int x, int y){
        int color = durability >= 1 ? BUFFER_COLOR : BUFFER_DULL_COLOR;
        int alpha = 255;//durabilityBuffer >= 1 ? 255 : 100;
        int width = (int) Math.ceil(Math.min(13, 13 * durability / (level * (float) Spellbound.config.BUFFER_MAX_PER_RANK)));

        renderGuiQuad(buffer, x + 2, y + 14, width, 1, color >> 16 & 255, color >> 8 & 255, color & 255, alpha);
    }

    @Environment(EnvType.CLIENT)
    private static void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex((double)(x + 0), (double)(y + 0), 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex((double)(x + 0), (double)(y + height), 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex((double)(x + width), (double)(y + height), 0.0D).color(red, green, blue, alpha).next();
        buffer.vertex((double)(x + width), (double)(y + 0), 0.0D).color(red, green, blue, alpha).next();
        BufferRenderer.drawWithShader(buffer.end());
    }

}
