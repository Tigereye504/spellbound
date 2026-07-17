package net.tigereye.spellbound.mob_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.UUID;

public class DyingEffect extends SBStatusEffect{

    public static final UUID DYING_HEATLH_ID = UUID.fromString("82d14a4a-b87f-45c7-a4b9-054a7357730b");
    private static final ResourceLocation DYING_OVERLAY = new ResourceLocation("textures/gui/dying_overlay.png");

    public DyingEffect(){
        super(MobEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public boolean isInstantenous() {
        return true;
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % Math.max(2,20>>amplifier) == 1;
    }
    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        AttributeInstance att = entity.getAttribute(Attributes.MAX_HEALTH);
        if(att != null) {
            AttributeModifier oldmod = att.getModifier(DYING_HEATLH_ID);
            double newValue = 0;
            if(oldmod != null) {
                newValue = oldmod.getAmount();
            }
            newValue -= Math.max(-.999,1d/Spellbound.config.lastGasp.SECONDS_TO_DIE);

            UpdateDyingModifier(entity,newValue);
            if(entity.getHealth() > entity.getMaxHealth()){
                entity.setHealth(Math.max(Math.min(entity.getMaxHealth(),entity.getHealth()-.1F),.1F)); //a tiny bit of health drain should force proper updates... for now.
            }

            if(newValue <= -.99){ //a bit of leeway to account for rounding errors
                entity.hurt(entity.damageSources().generic(),(entity.getMaxHealth()+entity.getAbsorptionAmount()) * 100);
                if(entity.isAlive() && entity.hasEffect(SBStatusEffects.DYING)){
                    entity.addEffect(new MobEffectInstance(SBStatusEffects.DYING, 53688
                            , 9,false,false,true));
                }
            }
        }
    }

    public void removeAttributeModifiers(LivingEntity entity, AttributeMap attributes, int amplifier) {
        AttributeInstance att = entity.getAttribute(Attributes.MAX_HEALTH);
        if(att != null) {
            AttributeModifier mod = att.getModifier(DYING_HEATLH_ID);
            if (mod != null) {
                if(mod.getAmount() > 0){
                    att.removeModifier(mod);
                }
            }
        }
    }

    public static void UpdateDyingModifier(LivingEntity entity, double newValue)
    {
        AttributeInstance att = entity.getAttribute(Attributes.MAX_HEALTH);
        AttributeModifier mod = new AttributeModifier(DYING_HEATLH_ID, "SpellboundDyingMaxHP",
                newValue
                , AttributeModifier.Operation.MULTIPLY_TOTAL);
        //removes any existing mod and replaces it with the updated one.
        if(att != null) {
            att.removeModifier(mod);
            att.addPermanentModifier(mod);
            if(!entity.level().isClientSide() && entity instanceof ServerPlayer sPlayer){
                sPlayer.resetSentInfo();
                sPlayer.onUpdateAbilities();
            }
        }
    }

    public static void renderDyingOverlay(GuiGraphics context, float delta){
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        if(player != null && player.hasEffect(SBStatusEffects.DYING)) {
            int duration = player.getEffect(SBStatusEffects.DYING).getDuration();
            int amplifier = player.getEffect(SBStatusEffects.DYING).getAmplifier();
            float opacity;
            if(duration == Spellbound.config.lastGasp.SECONDS_TO_DIE){
                opacity = .5f;
            }
            else{
                opacity = .15f + (.05f * amplifier);
            }
            RenderSystem.enableBlend();
            Minecraft.getInstance().gui.renderTextureOverlay(context,DYING_OVERLAY,opacity);
        }
    }
}
