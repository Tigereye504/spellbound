package net.tigereye.spellbound.registration;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.tigereye.spellbound.enchantments.protection.GraceEnchantment;
import net.tigereye.spellbound.mob_effect.Shielded;

public class SBGUI {

    public static void register(){
        HudRenderCallback.EVENT.register(Shielded::renderShields);
        HudRenderCallback.EVENT.register(GraceEnchantment::renderArmor);
    }
}
