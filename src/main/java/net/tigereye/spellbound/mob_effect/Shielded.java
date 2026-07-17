package net.tigereye.spellbound.mob_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.interfaces.SpellboundLivingEntity;
import net.tigereye.spellbound.registration.SBParticles;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.List;

public class Shielded extends SBStatusEffect{

    public static final ResourceLocation SHIELDED_HEART = new ResourceLocation(Spellbound.MODID,"textures/gui/shielded_heart.png");

    public Shielded(){
        super(MobEffectCategory.BENEFICIAL, 0x7CB5C6);
    }

    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    public void applyEffectTick(LivingEntity entity, int amplifier) {
        Vec3 basePosition = entity.position();
        Vec3 velocity = entity.getDeltaMovement();
        if(entity instanceof SpellboundLivingEntity slEntity && slEntity.spellbound$shouldDisplayShielded()) {
            Vec3 rotVec = entity.getLookAngle();
            Vec3 finalPos = basePosition.subtract(rotVec.normalize().scale(0.1));
            entity.level().addParticle(SBParticles.RED_ALERT_SHIELD,
                    finalPos.x, finalPos.y + 1, finalPos.z,
                    velocity.x, velocity.y, velocity.z);
        }
    }

    public float onPreArmorDefense(MobEffectInstance instance, DamageSource source, LivingEntity defender, float amount, List<MobEffectInstance> effectsToAdd, List<MobEffect> effectsToRemove){
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
            effectsToAdd.add(new MobEffectInstance(SBStatusEffects.SHIELDED, shieldDuration, shieldAmp, instance.isAmbient(), instance.isVisible(),instance.showIcon()));
        }
        return 0;
    }

    public static void renderShields(GuiGraphics drawContext, float delta){
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if(player != null && !(player.isCreative() || player.isSpectator())) {
            client.getProfiler().push("health");
            int scaledWidth = client.getWindow().getGuiScaledWidth();
            int scaledHeight = client.getWindow().getGuiScaledHeight();


            float maxHealth = Math.max((float) player.getAttributeValue(Attributes.MAX_HEALTH), 2);
            int absorption = Mth.ceil(player.getAbsorptionAmount());
            int lineMidValue = Mth.ceil((maxHealth + (float) absorption) / 2.0F / 10.0F);

            int x = scaledWidth / 2 - 92;
            int y = scaledHeight - 40;
            int lineWidth = Math.max(10 - (lineMidValue - 2), 3);
            int shieldLayers = 0;{
                if(player.hasEffect(SBStatusEffects.SHIELDED)){
                    shieldLayers = player.getEffect(SBStatusEffects.SHIELDED).getAmplifier()+1;
                }
            }

            int j = Mth.ceil((double) maxHealth / 2.0D);
            int k = Mth.ceil((double) absorption / 2.0D);
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
                    drawContext.blit(SHIELDED_HEART, posX, posY, 0, 0, 1, 11, 11, 11);
                }
                drawContext.blit(SHIELDED_HEART, posX+1, posY, 1, 0, 8, 11, 11, 11);
                if (isRightmost) {
                    drawContext.blit(SHIELDED_HEART, posX+9, posY, 9, 0, 2, 11, 11, 11);
                }
            }
            client.getProfiler().pop();
        }
    }
}
