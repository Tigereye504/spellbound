package net.tigereye.spellbound.enchantments.unbreaking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBComponents;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SpellboundUtil;

public class BufferedEnchantment extends SBEnchantment {

    public static final String BUFFER_TIME_KEY = Spellbound.MODID+":buffer_time";
    private static final int BUFFER_COLOR = 0x1cd8e3;
    private static final int BUFFER_DULL_COLOR = 0x579ca2;

    public BufferedEnchantment() {
        super(definition(ItemTags.DURABILITY_ENCHANTABLE,
            Spellbound.config.buffered.WEIGHT, //enchantment weight
            Spellbound.config.buffered.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.buffered.BASE_POWER,Spellbound.config.buffered.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.buffered.BASE_POWER+Spellbound.config.buffered.POWER_RANGE,Spellbound.config.buffered.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.buffered.ANVIL_COST, //level cost at anvil
            new EquipmentSlot[]{EquipmentSlot.MAINHAND}), //prefered slots
            false); //can work outside of prefered slot
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.buffered.ENABLED;
    }
    @Override
    public int getSoftLevelCap(){return Spellbound.config.buffered.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.buffered.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.buffered.IS_FOR_SALE;}

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack);
    }

    @Override
    public int beforeDurabilityLoss(int level, ItemStack stack, ServerPlayer entity, int loss){
        if(entity == null){
            return loss;
        }
        Level world = entity.level();
        if(!world.isClientSide()){
            float durabilityBuffer = getDurabilityBuffer(level, stack, entity.level());
            if(Spellbound.DEBUG) {
                Spellbound.LOGGER.info(stack.getHoverName().getString() + " has " + durabilityBuffer + " buffer");
            }
            if(durabilityBuffer >= 1){
                int cost = (int)Math.min(loss,Math.floor(durabilityBuffer));
                setDurabilityBuffer(level, stack, entity.level(), durabilityBuffer-cost);
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

    private static float getDurabilityBuffer(int level, ItemStack item, Level world){
        Long time = item.getOrDefault(SBComponents.BUFFERED_TIME,0l);
        return getDurabilityBuffer(level, time, world.getGameTime());
    }

    private static float getDurabilityBuffer(ItemStack item, Level world){
        return getDurabilityBuffer(EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.BUFFERED,item),item,world);
    }

    private static void setDurabilityBuffer(int level, ItemStack item, Level world, float buffer){
        long timeDiff = (long) (buffer * Spellbound.config.buffered.RECOVERY_RATE)/level;
        item.set(SBComponents.BUFFERED_TIME,world.getGameTime()-timeDiff);
    }

    @Environment(EnvType.CLIENT)
    public static void RenderBufferItemOverlay(GuiGraphics drawContext, ItemStack stack, int x, int y){
        Level world = Minecraft.getInstance().level;
        int level = EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.BUFFERED,stack);
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
    private static void renderBufferAsAura(GuiGraphics drawContext, float durability, int x, int y){

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

            drawContext.fill(RenderType.guiOverlay(), x + i, y + i, x + i + longSide, y + i + 1, color | alpha << 24);
            drawContext.fill(RenderType.guiOverlay(), x + 15 - i, y + i, x + 16 - i, y + i + longSide, color | alpha << 24);
            drawContext.fill(RenderType.guiOverlay(), x + i, y + 1 + i, x + i + 1, y + 1 + i + longSide, color | alpha << 24);
            drawContext.fill(RenderType.guiOverlay(), x + 1 + i, y + 15 - i, x + 1 + i + longSide, y + 16 - i, color | alpha << 24);
            durability--;
        }
    }
    
    @Environment(EnvType.CLIENT)
    private static void renderBufferAsBar(GuiGraphics drawContext, int level, float durability, int x, int y){
        int color = durability >= 1 ? BUFFER_COLOR : BUFFER_DULL_COLOR;
        int alpha = 255;//durabilityBuffer >= 1 ? 255 : 100;
        int width = (int) Math.ceil(Math.min(13, 13 * durability / (level * (float) Spellbound.config.buffered.MAX_PER_RANK)));

        drawContext.fill(RenderType.guiOverlay(), x + 2, y + 14, x + 2 + width, y + 15, color | alpha << 24);
    }

}
