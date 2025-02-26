package net.tigereye.spellbound.enchantments.protection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class FleshWoundEnchantment extends SBEnchantment{

    public int breakpointRenderIntervals = 1;
    public int breakpointRenderCurrent = 0;

    public static final Identifier FLESH_WOUND_BREAKPOINT = new Identifier(Spellbound.MODID,"textures/gui/flesh_wound_breakpoint.png");
    private static final String FLESH_WOUND_NBT_KEY = "SB_FleshWound";

    public FleshWoundEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.fleshWound.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.fleshWound.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.fleshWound.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.fleshWound.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.fleshWound.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.fleshWound.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.fleshWound.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.fleshWound.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.fleshWound.IS_FOR_SALE;}

    //TODO: add GUI layer for Flesh Wound

    public void onEquipmentChangeOnce(int oldLevel, int newLevel, ItemStack oldItem, ItemStack newItem, LivingEntity entity){
        if(oldLevel != newLevel) {
            Iterable<ItemStack> gear = entity.getItemsEquipped();
            for(ItemStack item : gear){
                if(EnchantmentHelper.getLevel(SBEnchantments.FLESH_WOUND,item) > 0){
                    item.getOrCreateNbt().putInt(FLESH_WOUND_NBT_KEY,0);
                }
            }
            if(entity.getWorld().isClient()){
                if(entity == MinecraftClient.getInstance().player){
                    breakpointRenderIntervals = 1;
                    breakpointRenderCurrent = 0;
                }
            }
        }
    }

    @Override
    public void onTickOnceWhileEquipped(int level, ItemStack stack, LivingEntity entity){
        int enchantmentCount = SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getItemsEquipped(), SBEnchantments.FLESH_WOUND,entity);
        int activeBreakpoints = stack.getOrCreateNbt().getInt(FLESH_WOUND_NBT_KEY);
        int breakpointDelta = updateBreakPointLevel(stack,entity);
        if(!entity.getWorld().isClient()) {
            int totalRanksActivated = 0;
            float oldAbsorptionAmount = entity.getAbsorptionAmount();
            if (breakpointDelta < 0) {
                totalRanksActivated = getNthtoMthEnchantmentLevels(enchantmentCount - activeBreakpoints + 1, enchantmentCount - activeBreakpoints - breakpointDelta, entity);
                entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.BRAVADOS, Spellbound.config.fleshWound.DURATION, 0, false, false, false));
            }
            entity.setAbsorptionAmount(oldAbsorptionAmount + (entity.getMaxHealth() * totalRanksActivated * Spellbound.config.fleshWound.ABSORPTION_RATIO_PER_RANK));
        }
    }

    private int updateBreakPointLevel(ItemStack stack, LivingEntity entity){

        int oldBreakpointCount = stack.getOrCreateNbt().getInt(FLESH_WOUND_NBT_KEY);
        int newBreakpointCount = oldBreakpointCount;
        int intervals = SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getItemsEquipped(),SBEnchantments.FLESH_WOUND, entity)+1;
        //determine the most break points the current health level can support
        int breakPointsPossible = (int) Math.floor(entity.getHealth() / entity.getMaxHealth()
                * intervals);
        if(breakPointsPossible-1 >= oldBreakpointCount){
            newBreakpointCount = breakPointsPossible-1;
        }
        else if(oldBreakpointCount > breakPointsPossible){
            newBreakpointCount = breakPointsPossible;
        }
        if(oldBreakpointCount != newBreakpointCount) {
            stack.getOrCreateNbt().putInt(FLESH_WOUND_NBT_KEY, newBreakpointCount);
        }
        if(entity.getWorld().isClient()){
            if(entity == MinecraftClient.getInstance().player){
                breakpointRenderIntervals = intervals;
                breakpointRenderCurrent = newBreakpointCount;
            }
        }
        return newBreakpointCount - oldBreakpointCount;
    }

    private int getNthtoMthEnchantmentLevels(int n, int m, LivingEntity entity){
        int counter = 0;
        int total = 0;
        for(ItemStack item : entity.getItemsEquipped()){
            if(counter > m){
                return total;
            }
            if(SBEnchantmentHelper.isEquipmentCorrectlyWorn(item,entity)){
                int itemLevel = EnchantmentHelper.getLevel(SBEnchantments.FLESH_WOUND, item);
                if(itemLevel > 0) {
                    counter++;
                    if(counter >= n && counter <= m) {
                        total += itemLevel;
                    }
                }
            }
        }
        return total;
    }

    public static void renderBreakpoints(DrawContext drawContext, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && !(player.isCreative() || player.isSpectator())) {
            client.getProfiler().push("health");
            int scaledWidth = client.getWindow().getScaledWidth();
            int scaledHeight = client.getWindow().getScaledHeight();

            float maxHealth = Math.max((float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), 2);
            int absorption = MathHelper.ceil(player.getAbsorptionAmount());
            int lineMidValue = MathHelper.ceil((maxHealth + (float) absorption) / 2.0F / 10.0F);

            int x = scaledWidth / 2 - 92;
            int y = scaledHeight - 40;
            int lineWidth = Math.max(10 - (lineMidValue - 2), 3);
            RenderSystem.enableBlend();
            for (int m = SBEnchantments.FLESH_WOUND.breakpointRenderCurrent - 1; m >= 0; --m) {
                float breakpointHealth = (float) (m + 1) / SBEnchantments.FLESH_WOUND.breakpointRenderIntervals * maxHealth;
                int n = (int) Math.floor(breakpointHealth / 20); //vertical offset in rows
                int o = (int) Math.ceil(breakpointHealth % 20); //horizontal offset in columns.
                int posX = x + (o * 4) - 1;
                int posY = y - (n * lineWidth); //height of row n
                drawContext.drawTexture(FLESH_WOUND_BREAKPOINT, posX, posY, 0, 0, 5, 9, 5, 9);
            }
            client.getProfiler().pop();
        }
    }
}
