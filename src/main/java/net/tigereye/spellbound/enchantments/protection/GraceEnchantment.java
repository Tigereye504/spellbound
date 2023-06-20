package net.tigereye.spellbound.enchantments.protection;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.enchantments.CustomConditionsEnchantment;
import net.tigereye.spellbound.enchantments.SBEnchantment;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBEnchantments;
import net.tigereye.spellbound.util.SBEnchantmentHelper;
import net.tigereye.spellbound.util.SpellboundUtil;

public class GraceEnchantment extends SBEnchantment implements CustomConditionsEnchantment {
    public static final Identifier GRACE_ARMOR = new Identifier(Spellbound.MODID,"textures/gui/grace_armor.png");
    public GraceEnchantment() {
        super(SpellboundUtil.rarityLookup(Spellbound.config.GRACE_RARITY), EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.OFFHAND});
        REQUIRES_PREFERRED_SLOT = true;
    }

    @Override
    public boolean isEnabled() {
        return Spellbound.config.GRACE_ENABLED;
    }

    @Override
    public int getMinPower(int level) {
        int power = (Spellbound.config.GRACE_POWER_PER_RANK * level) - Spellbound.config.GRACE_BASE_POWER;
        if(level > Spellbound.config.GRACE_SOFT_CAP) {
            power += Spellbound.config.POWER_TO_EXCEED_SOFT_CAP;
        }
        return power;
    }

    @Override
    public int getMaxPower(int level) {
        return super.getMinPower(level) + Spellbound.config.GRACE_POWER_RANGE;
    }

    @Override
    public int getMaxLevel() {
        if(isEnabled()) return Spellbound.config.GRACE_HARD_CAP;
        else return 0;
    }

    @Override
    public boolean isAcceptableItem(ItemStack stack) {
        return isAcceptableAtTable(stack);
    }

    @Override
    public int getIFrameAmount(int level, int frames, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return frames + (level*Spellbound.config.GRACE_IFRAME_TICKS_PER_LEVEL);
    }

    @Override
    public float getIFrameMagnitude(int level, float magnitude, DamageSource source, float damageAmount, ItemStack itemStack, LivingEntity defender) {
        return magnitude * (1+(level*Spellbound.config.GRACE_IFRAME_MAGNITUDE_PER_LEVEL));
    }

    @Override
    public boolean isAcceptableAtTable(ItemStack stack) {
        return stack.getItem() instanceof ArmorItem
                || stack.getItem() instanceof ShieldItem
                || stack.getItem() == Items.BOOK;
    }

    public static void renderArmor(MatrixStack matrixStack, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && !(player.isCreative() || player.isSpectator())) {
            float graceAmount = ((SpellboundLivingEntity)player).getGraceMagnitude();
            int graceTicks = ((SpellboundLivingEntity)player).getGraceTicks();
            if(graceAmount > 0 && graceTicks > 0 && SBEnchantmentHelper.getSpellboundEnchantmentAmount(player.getArmorItems(),SBEnchantments.GRACE) > 0) {
                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                matrixStack.push();
                RenderSystem._setShaderTexture(0, GRACE_ARMOR);
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
                int fadeLevel = Math.min(Math.max(3 - ((graceTicks-1) / (Spellbound.config.GRACE_IFRAME_TICKS_PER_LEVEL*5)), 0),3);
                for (int w = 0; w < 10; ++w) {
                    x = m + w * 8;
                    if (w * 2 + 1 < graceAmount) {
                        DrawableHelper.drawTexture(matrixStack, x, s, 0, fadeLevel*9, 9, 9, 18, 36);

                    } else if (w * 2 < graceAmount) {
                        DrawableHelper.drawTexture(matrixStack, x, s, 9, fadeLevel*9, 9, 9, 18, 36);
                    }
                }

                matrixStack.pop();

                RenderSystem.enableDepthTest();
            }
        }
    }
}
