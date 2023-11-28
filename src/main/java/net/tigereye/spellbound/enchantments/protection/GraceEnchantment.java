package net.tigereye.spellbound.enchantments.protection;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantmentTargets;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class GraceEnchantment extends SBEnchantment{
    public static final Identifier GRACE_ARMOR = new Identifier(Spellbound.MODID,"textures/gui/grace_armor.png");
    public GraceEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.grace.RARITY), SBEnchantmentTargets.ARMOR_MAYBE_SHIELD,
                Spellbound.config.CAN_SHIELD_HAVE_ARMOR_ENCHANTMENTS
                        ? new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND}
                        : new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET}
                ,true);
    }
    @Override
    public boolean isEnabled() {return Spellbound.config.grace.ENABLED;}
    @Override
    public int getSoftLevelCap(){return Spellbound.config.grace.SOFT_CAP;}
    @Override
    public int getHardLevelCap(){return Spellbound.config.grace.HARD_CAP;}
    @Override
    public int getBasePower(){return Spellbound.config.grace.BASE_POWER;}
    @Override
    public int getPowerPerRank(){return Spellbound.config.grace.POWER_PER_RANK;}
    @Override
    public int getPowerRange(){return Spellbound.config.grace.POWER_RANGE;}
    @Override
    public boolean isTreasure() {return Spellbound.config.grace.IS_TREASURE;}
    @Override
    public boolean isAvailableForEnchantedBookOffer(){return Spellbound.config.grace.IS_FOR_SALE;}

    @Override
    public int getIFrameAmount(int level, int frames, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return frames + (level*Spellbound.config.grace.IFRAME_TICKS_PER_LEVEL);
    }

    @Override
    public float getIFrameMagnitude(int level, float magnitude, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return magnitude * (1+(level*Spellbound.config.grace.IFRAME_MAGNITUDE_PER_LEVEL));
    }

    public static void renderArmor(DrawContext drawContext, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && !(player.isCreative() || player.isSpectator())) {
            float graceAmount = ((SpellboundLivingEntity)player).spellbound$getGraceMagnitude();
            int graceTicks = ((SpellboundLivingEntity)player).spellbound$getGraceTicks();
            if(graceAmount > 0 && graceTicks > 0 && SBEnchantmentHelper.getSpellboundEnchantmentAmount(player.getArmorItems(),SBEnchantments.GRACE) > 0) {
                client.getProfiler().push("armor");
                int scaledWidth = client.getWindow().getScaledWidth();
                int scaledHeight = client.getWindow().getScaledHeight();

                float f = Math.max((float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), 2);
                int m = scaledWidth / 2 - 91;

                int o = scaledHeight - 39;
                int p = MathHelper.ceil(player.getAbsorptionAmount());
                int q = MathHelper.ceil((f + (float) p) / 2.0F / 10.0F);
                int r = Math.max(10 - (q - 2), 3);
                int s = o - (q - 1) * r - 10;
                int x;
                int fadeLevel = Math.min(Math.max(3 - ((graceTicks-1) / (Spellbound.config.grace.IFRAME_TICKS_PER_LEVEL*5)), 0),3);
                for (int w = 0; w < 10; ++w) {
                    x = m + w * 8;
                    if (w * 2 + 1 < graceAmount) {
                        drawContext.drawTexture(GRACE_ARMOR, x, s, 0, fadeLevel*9, 9, 9, 18, 36);

                    } else if (w * 2 < graceAmount) {
                        drawContext.drawTexture(GRACE_ARMOR, x, s, 9, fadeLevel*9, 9, 9, 18, 36);
                    }
                }
                client.getProfiler().pop();
            }
        }
    }
}
