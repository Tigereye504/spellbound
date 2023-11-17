package net.tigereye.spellbound.mob_effect;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectCategory;
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
        return true;
    }
    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if(!(entity.getWorld().isClient)){
            EntityAttributeInstance att = entity.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
            if(att != null) {
                EntityAttributeModifier oldmod = att.getModifier(DYING_HEATLH_ID);
                double newValue = 0;
                if(oldmod != null) {
                newValue = oldmod.getValue();
                }
                newValue -= (Math.pow(2,amplifier)/Spellbound.config.lastGasp.TIME_TO_DIE);
                UpdateDyingModifier(entity,newValue);
                if(entity.getHealth() > entity.getMaxHealth() && newValue > -1){
                    entity.setHealth(entity.getMaxHealth());
                }

                if(newValue <= -1){
                    //entity.setHealth(0.01f);
                    entity.damage(entity.getDamageSources().genericKill(),999);
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
        }
    }

    public static void renderDyingOverlay(DrawContext context, float delta){
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayerEntity player = client.player;
        if(player != null && player.hasStatusEffect(SBStatusEffects.DYING)) {
            int duration = player.getStatusEffect(SBStatusEffects.DYING).getDuration();
            int amplifier = player.getStatusEffect(SBStatusEffects.DYING).getAmplifier();
            float opacity;
            if(duration == Spellbound.config.lastGasp.TIME_TO_DIE){
                opacity = .5f;
            }
            else{
                opacity = .15f + (.05f * amplifier);
            }
            MinecraftClient.getInstance().inGameHud.renderOverlay(context,DYING_OVERLAY,opacity);
        }
    }
}
