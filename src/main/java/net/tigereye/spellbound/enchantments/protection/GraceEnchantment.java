package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.registration.SBTags;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class GraceEnchantment extends SBEnchantment{
    public static final ResourceLocation GRACE_ARMOR = new ResourceLocation(Spellbound.MODID,"textures/gui/grace_armor.png");
    public GraceEnchantment() {
        super(definition(Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS ? SBTags.ARMOR_AND_SHIELD_ENCHANTABLE : ItemTags.ARMOR_ENCHANTABLE,
            Spellbound.config.grace.WEIGHT, //enchantment weight
            Spellbound.config.grace.HARD_CAP, //level cap
            dynamicCost(Spellbound.config.grace.BASE_POWER,Spellbound.config.grace.POWER_PER_RANK), //minimum enchanting power to roll
            dynamicCost(Spellbound.config.grace.BASE_POWER+Spellbound.config.grace.POWER_RANGE,Spellbound.config.grace.POWER_PER_RANK), //maximum enchanting power to roll
            Spellbound.config.grace.ANVIL_COST, //level cost at anvil
            Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}), //prefered slots
            true); //can work outside of prefered slot
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.grace.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.grace.SOFT_CAP;}
    @Override
    public boolean isTreasureOnly() {return Spellbound.config.grace.IS_TREASURE;}
    @Override
    public boolean isTradeable(){return Spellbound.config.grace.IS_FOR_SALE;}

    @Override
    public int getIFrameAmount(int level, int frames, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return frames + (level*Spellbound.config.grace.IFRAME_TICKS_PER_LEVEL);
    }

    @Override
    public float getIFrameMagnitude(int level, float magnitude, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return magnitude * (1+(level*Spellbound.config.grace.IFRAME_MAGNITUDE_PER_LEVEL));
    }

    public static void renderArmor(GuiGraphics drawContext, float delta){
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if(player != null && !(player.isCreative() || player.isSpectator())) {
            float graceAmount = ((SpellboundLivingEntity)player).spellbound$getGraceMagnitude();
            int graceTicks = ((SpellboundLivingEntity)player).spellbound$getGraceTicks();
            if(graceAmount > 0 && graceTicks > 0 && SBEnchantmentHelper.getSpellboundEnchantmentAmount(player.getArmorSlots(),SBEnchantments.GRACE) > 0) {
                client.getProfiler().push("armor");
                int scaledWidth = client.getWindow().getGuiScaledWidth();
                int scaledHeight = client.getWindow().getGuiScaledHeight();

                float f = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), 2);
                int m = scaledWidth / 2 - 91;

                int o = scaledHeight - 39;
                int p = Mth.ceil(player.getAbsorptionAmount());
                int q = Mth.ceil((f + (float) p) / 2.0F / 10.0F);
                int r = Math.max(10 - (q - 2), 3);
                int s = o - (q - 1) * r - 10;
                int x;
                int fadeLevel = Math.min(Math.max(3 - ((graceTicks-1) / (Spellbound.config.grace.IFRAME_TICKS_PER_LEVEL*5)), 0),3);
                for (int w = 0; w < 10; ++w) {
                    x = m + w * 8;
                    if (w * 2 + 1 < graceAmount) {
                        drawContext.blit(GRACE_ARMOR, x, s, 0, fadeLevel*9, 9, 9, 18, 36);

                    } else if (w * 2 < graceAmount) {
                        drawContext.blit(GRACE_ARMOR, x, s, 9, fadeLevel*9, 9, 9, 18, 36);
                    }
                }
                client.getProfiler().pop();
            }
        }
    }
}
