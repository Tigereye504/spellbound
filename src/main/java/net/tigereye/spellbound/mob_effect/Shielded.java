package net.tigereye.spellbound.mob_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
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

    public static void renderShields(MatrixStack matrixStack, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(!(player.isCreative() || player.isSpectator())) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            matrixStack.push();
            RenderSystem._setShaderTexture(0, SHIELDED_HEART);
            int scaledWidth = client.getWindow().getScaledWidth();
            int scaledHeight = client.getWindow().getScaledHeight();


            float maxHealth = Math.max((float) player.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), 2);
            int absorption = MathHelper.ceil(player.getAbsorptionAmount());
            int lineMidValue = MathHelper.ceil((maxHealth + (float) absorption) / 2.0F / 10.0F);

            int x = scaledWidth / 2 - 92;
            int y = scaledHeight - 40;
            int lines = Math.max(10 - (lineMidValue - 2), 3);
            int displayedShields = 0;{
                if(player.hasStatusEffect(SBStatusEffects.SHIELDED)){
                    displayedShields = player.getStatusEffect(SBStatusEffects.SHIELDED).getAmplifier()+1;
                }
            }

            int i = 9 * (player.world.getLevelProperties().isHardcore() ? 5 : 0);
            int j = MathHelper.ceil((double) maxHealth / 2.0D);
            int k = MathHelper.ceil((double) absorption / 2.0D);
            int l = j * 2;


            for (int m = j + k - 1; m >= 0; --m) {
                int n = m / 10;
                int o = m % 10;
                int posX = x + o * 8;
                int posY = y - n * lines;

                //int r = m * 2;

                if (m < displayedShields) {
                    DrawableHelper.drawTexture(matrixStack, posX, posY, 0, 0, 11, 11, 11, 11);
                    //this.drawHeart(matrixStack, heartType, posX, posY, i, false, isHalfHeart);
                }
            }
            matrixStack.pop();

            RenderSystem.enableDepthTest();
        }
    }
}
