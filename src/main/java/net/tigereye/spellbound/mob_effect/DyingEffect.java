package net.tigereye.spellbound.mob_effect;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.UUID;

public class DyingEffect extends SBStatusEffect{

    public static final UUID DYING_HEATLH_ID = UUID.fromString("82d14a4a-b87f-45c7-a4b9-054a7357730b");
    private static final Identifier DYING_OVERLAY = new Identifier("textures/gui/dying_overlay.png");

    public DyingEffect(){
        super(StatusEffectCategory.HARMFUL, 0xFF0000);
    }

    @Override
    public boolean isInstant() {
        return true;
    }
    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return duration % Math.max(2,20>>amplifier) == 1;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            EntityAttributeModifier oldmod = att.getModifier(DYING_HEATLH_ID);
            double newValue = 0;
            if(oldmod != null) {
                newValue = oldmod.getValue();
            }
            newValue -= Math.max(-.999,1d/Spellbound.config.lastGasp.SECONDS_TO_DIE);

            UpdateDyingModifier(entity,newValue);
            if(entity.getHealth() > entity.getMaxHealth()){
                entity.setHealth(entity.getMaxHealth());
            }

            if(newValue <= -.99){ //a bit of leeway to account for rounding errors
                entity.damage(entity.getDamageSources().generic(),entity.getMaxHealth() * 100);
                if(entity.isAlive() && entity.hasStatusEffect(SBStatusEffects.DYING)){
                    entity.addStatusEffect(new StatusEffectInstance(SBStatusEffects.DYING, 53688
                            , 9,false,false,true));
                }
            }
        }
    }

    public void onRemoved(LivingEntity entity, AttributeContainer attributes, int amplifier) {
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(att != null) {
            EntityAttributeModifier mod = att.getModifier(DYING_HEATLH_ID);
            if (mod != null) {
                if(mod.getValue() > 0){
                    att.removeModifier(mod);
                }
            }
        }
    }

    public static void UpdateDyingModifier(LivingEntity entity, double newValue)
    {
        EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        EntityAttributeModifier mod = new EntityAttributeModifier(DYING_HEATLH_ID, "SpellboundDyingMaxHP",
                newValue
                , EntityAttributeModifier.Operation.MULTIPLY_TOTAL);
        //removes any existing mod and replaces it with the updated one.
        if(att != null) {
            att.removeModifier(mod);
            att.addPersistentModifier(mod);
            if(!entity.getWorld().isClient() && entity instanceof ServerPlayerEntity sPlayer){
                sPlayer.markHealthDirty();
            }
        }
    }

    public static void renderDyingOverlay(DrawContext context, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && player.hasStatusEffect(SBStatusEffects.DYING)) {
            int duration = player.getStatusEffect(SBStatusEffects.DYING).getDuration();
            int amplifier = player.getStatusEffect(SBStatusEffects.DYING).getAmplifier();
            float opacity;
            if(duration == Spellbound.config.lastGasp.SECONDS_TO_DIE){
                opacity = .5f;
            }
            else{
                opacity = .15f + (.05f * amplifier);
            }
            RenderSystem.enableBlend();
            MinecraftClient.getInstance().inGameHud.renderOverlay(context,DYING_OVERLAY,opacity);
        }
    }
}
