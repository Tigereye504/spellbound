package net.tigereye.spellbound.registration;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.Shielded;

public class SBGUI {

    public static void register(){
        HudRenderCallback.EVENT.register(Shielded::renderShields);
    }
}
