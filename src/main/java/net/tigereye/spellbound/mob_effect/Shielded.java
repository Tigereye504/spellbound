package net.tigereye.spellbound.mob_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.List;

public class Shielded extends SBStatusEffect{

    public static final Identifier SHIELDED_HEART = new Identifier(Spellbound.MODID,"textures/gui/shielded_heart.png");

    public Shielded(){
        super(StatusEffectCategory.BENEFICIAL, 0x7CB5C6);
    }

    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return false;
    }

    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
    }

    public float onPreArmorDefense(StatusEffectInstance instance, DamageSource source, LivingEntity defender, float amount, List<StatusEffectInstance> effectsToAdd, List<StatusEffect> effectsToRemove){
        if(amount <= 0){
            return amount;
        }
        else if(instance.getAmplifier() == 0){
            effectsToRemove.add(SBStatusEffects.SHIELDED);
        }
        else{
            int shieldDuration = instance.getDuration();
            int shieldAmp = instance.getAmplifier()-1;
            effectsToRemove.add(SBStatusEffects.SHIELDED);
            effectsToAdd.add(new StatusEffectInstance(SBStatusEffects.SHIELDED, shieldDuration, shieldAmp, instance.isAmbient(), instance.shouldShowParticles(),instance.shouldShowIcon()));
        }
        return 0;
    }

    public static void renderShields(DrawContext drawContext, float delta){
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
            int shieldLayers = 0;{
                if(player.hasStatusEffect(SBStatusEffects.SHIELDED)){
                    shieldLayers = player.getStatusEffect(SBStatusEffects.SHIELDED).getAmplifier()+1;
                }
            }

            int j = MathHelper.ceil((double) maxHealth / 2.0D);
            int k = MathHelper.ceil((double) absorption / 2.0D);
            int displayableShields = Math.min(shieldLayers,j + k);
            RenderSystem.enableBlend();
            for (int m = displayableShields - 1; m >= 0; --m) {

                int n = m / 10;
                int o = m % 10;
                int posX = x + o * 8;
                int posY = y - n * lineWidth;
                boolean isRightmost = o == 9 || m == displayableShields - 1;
                boolean isLeftmost = o == 0;
                if (isLeftmost) {
                    drawContext.drawTexture(SHIELDED_HEART, posX, posY, 0, 0, 1, 11, 11, 11);
                }
                drawContext.drawTexture(SHIELDED_HEART, posX+1, posY, 1, 0, 8, 11, 11, 11);
                if (isRightmost) {
                    drawContext.drawTexture(SHIELDED_HEART, posX+9, posY, 9, 0, 2, 11, 11, 11);
                }
                /*
                if(isLeftmost && isRightmost){
                    drawContext.drawTexture(SHIELDED_HEART, posX, posY, 0, 0, 11, 11, 40, 11);
                }
                else if (!isLeftmost && !isRightmost) {
                    drawContext.drawTexture(SHIELDED_HEART, posX+1, posY, 20, 0, 9, 11, 40, 11);
                }
                else if (isLeftmost) {
                    drawContext.drawTexture(SHIELDED_HEART, posX, posY, 11, 0, 9, 11, 40, 11);
                }
                else {
                    drawContext.drawTexture(SHIELDED_HEART, posX+2, posY, 29, 0, 9, 11, 40, 11);
                }
                */
            }
            client.getProfiler().pop();
        }
    }
}
