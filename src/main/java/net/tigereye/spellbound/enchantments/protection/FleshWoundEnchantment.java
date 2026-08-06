package net.tigereye.spellbound.enchantments.protection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBStatusEffects;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class FleshWoundEnchantment extends SBEnchantment{

    public static final ResourceLocation FLESH_WOUND_BREAKPOINT = new ResourceLocation(Spellbound.MODID,"textures/gui/flesh_wound_breakpoint.png");

    public FleshWoundEnchantment() {
        super(definition(Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS ? SBTags.ARMOR_AND_SHIELD_ENCHANTABLE : ItemTags.ARMOR_ENCHANTABLE,
            SpellboundUtil.rarityLookup(Spellbound.config.fleshWound.RARITY), //enchantment weight
            Spellbound.config.fleshWound.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.fleshWound.BASE_POWER,Spellbound.config.fleshWound.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.fleshWound.BASE_POWER+Spellbound.config.fleshWound.POWER_RANGE,Spellbound.config.fleshWound.POWER_PER_RANK), //maximum enchanting power to roll
            (int)Math.pow(2,Spellbound.config.fleshWound.RARITY-1), //level cost at anvil
            Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.fleshWound.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.fleshWound.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.fleshWound.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.fleshWound.IS_FOR_SALE;}

    @Override
    public void onTakeRedHealthDamageOnce(int level, ItemStack stack, DamageSource source, LivingEntity entity, float amount) {
        float oldHealth = entity.getHealth()+amount;
        int enchantmentCount = SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getAllSlots(),SBEnchantments.FLESH_WOUND, entity);
        boolean missingBreakpoint = isMissingBreakpoint(entity);
        int oldBreakPoints = (int) Math.floor(oldHealth / entity.getMaxHealth() * (enchantmentCount+1));
        int newBreakPoints = (int) Math.floor(entity.getHealth() / entity.getMaxHealth() * (enchantmentCount+1));

        if(newBreakPoints < oldBreakPoints){
            int pointsBroken = oldBreakPoints - newBreakPoints - (missingBreakpoint ? 1 : 0);
            float newAbsorption = 0;
            if(pointsBroken > 0){
                int totalRanksActivated = getNthtoMthEnchantmentLevels(
                    enchantmentCount - (oldBreakPoints - (missingBreakpoint ? 1 : 0)) + 1, 
                    enchantmentCount - newBreakPoints, 
                    entity);
                newAbsorption = entity.getMaxHealth() * totalRanksActivated * Spellbound.config.fleshWound.ABSORPTION_RATIO_PER_RANK;
            }
            updateBravados(entity,false,newAbsorption);

        }
    }

    @Override
    public void afterHealOnce(int level, ItemStack stack, LivingEntity entity, float amount) {
        float oldHealth = entity.getHealth()-amount;
        int enchantmentCount = SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(entity.getAllSlots(),SBEnchantments.FLESH_WOUND, entity);
        int oldBreakPoints = (int) Math.floor(oldHealth / entity.getMaxHealth() * (enchantmentCount+1));
        int newBreakPoints = (int) Math.floor(entity.getHealth() / entity.getMaxHealth() * (enchantmentCount+1));

        if(newBreakPoints > oldBreakPoints){
            updateBravados(entity,true);
        }
    }

    private boolean isMissingBreakpoint(LivingEntity entity) {
        if(!entity.hasEffect(SBStatusEffects.BRAVADOS)){
            return false;
        }
        return entity.getEffect(SBStatusEffects.BRAVADOS).getAmplifier() == 1;
    }

    private void updateBravados(LivingEntity entity, boolean missingBreakpoint) {
        updateBravados(entity,missingBreakpoint,entity.getAbsorptionAmount());
    }
    private void updateBravados(LivingEntity entity, boolean missingBreakpoint, float absorptionAmount) {
        entity.removeEffect(SBStatusEffects.BRAVADOS);
        entity.addEffect(new MobEffectInstance(SBStatusEffects.BRAVADOS, Spellbound.config.fleshWound.DURATION, missingBreakpoint ? 1 : 0, false, false, false));
        entity.setAbsorptionAmount(absorptionAmount);
    }

    //Sums the enchantment's levels from the nth equipped item to the mth equipped item
    private int getNthtoMthEnchantmentLevels(int n, int m, LivingEntity entity){
        int counter = 0;
        int total = 0;
        for(ItemStack item : entity.getAllSlots()){
            if(counter > m){
                return total;
            }
            if(SBEnchantmentHelper.isEquipmentCorrectlyWorn(item,entity)){
                int itemLevel = EnchantmentHelper.getItemEnchantmentLevel(SBEnchantments.FLESH_WOUND, item);
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

    public static void renderBreakpoints(GuiGraphics drawContext, float delta){
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if(player != null && !(player.isCreative() || player.isSpectator())) {
            int enchantmentCount = SBEnchantmentHelper.countSpellboundEnchantmentInstancesCorrectlyWorn(player.getAllSlots(),SBEnchantments.FLESH_WOUND, player) + 1;
            if(enchantmentCount <= 0){
                return;
            }
            int activeBreakpoints = (int) Math.floor(player.getHealth() / player.getMaxHealth() * (enchantmentCount+1));
            if(activeBreakpoints <= 0){
                return;
            }
            client.getProfiler().push("health");
            int scaledWidth = client.getWindow().getGuiScaledWidth();
            int scaledHeight = client.getWindow().getGuiScaledHeight();

            float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), 2);
            int absorption = Mth.ceil(player.getAbsorptionAmount());
            int lineMidValue = Mth.ceil((maxHealth + (float) absorption) / 2.0F / 10.0F);

            int x = scaledWidth / 2 - 92;
            int y = scaledHeight - 40;
            int lineWidth = Math.max(10 - (lineMidValue - 2), 3);
            RenderSystem.enableBlend();
            for (int m = activeBreakpoints - 1; m >= 0; --m) {
                float breakpointHealth = (float) (m + 1) / (enchantmentCount+1) * maxHealth;
                int n = (int) Math.floor(breakpointHealth / 20); //vertical offset in rows
                int o = (int) Math.ceil(breakpointHealth % 20); //horizontal offset in columns.
                int posX = x + (o * 4) - 1;
                int posY = y - (n * lineWidth); //height of row n
                drawContext.blit(FLESH_WOUND_BREAKPOINT, posX, posY, 0, 0, 5, 9, 5, 9);
            }
            client.getProfiler().pop();
        }
    }
}
