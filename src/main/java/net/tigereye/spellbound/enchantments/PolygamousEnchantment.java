package net.tigereye.spellbound.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.tigereye.spellbound.Spellbound;
import net.tigereye.spellbound.mob_effect.instance.MonogamyInstance;
import net.tigereye.spellbound.mob_effect.instance.PolygamyInstance;
import net.tigereye.spellbound.registration.SBConfig;
import net.tigereye.spellbound.registration.SBStatusEffects;

import java.util.UUID;

public class PolygamousEnchantment extends SBEnchantment{

    public PolygamousEnchantment() {
        super(Rarity.UNCOMMON, EnchantmentTarget.VANISHABLE, new EquipmentSlot[] {EquipmentSlot.HEAD,EquipmentSlot.CHEST,EquipmentSlot.LEGS,EquipmentSlot.FEET,EquipmentSlot.MAINHAND,EquipmentSlot.OFFHAND});
    }

    public int getMinPower(int level) {
        return 5;
    }

    public int getMaxPower(int level) {
        return 51;
    }

    public int getMaxLevel() {
        return 1;
    }

    public boolean isAcceptableItem(ItemStack stack) {
        return super.isAcceptableItem(stack);
    }

    public float getProtectionAmount(int level, DamageSource source, ItemStack stack, LivingEntity target) {
        testOwnerFaithfulness(stack,target);
        if(target.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            return .5f;
        }
        return -1;
    }

    public float getAttackDamage(int level, ItemStack stack, LivingEntity attacker, Entity defender) {
        testOwnerFaithfulness(stack,attacker);
        if(attacker.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            return 2;
        }
        return -4;
    }

    public float getProjectileDamage(int level, ItemStack stack, PersistentProjectileEntity projectile, Entity attacker, Entity defender, float damage) {
        if(attacker instanceof LivingEntity) {
            testOwnerFaithfulness(stack, (LivingEntity)attacker);
            if (((LivingEntity)attacker).hasStatusEffect(SBStatusEffects.POLYGAMY)) {
                return damage*1.1f;
            }
            return damage*.8f;
        }
        return damage;
    }

    public float getMiningSpeed(int level, PlayerEntity playerEntity, ItemStack itemStack, BlockState block, float miningSpeed) {
        testOwnerFaithfulness(itemStack,playerEntity);
        if(playerEntity.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            return miningSpeed*1.2f;
        }
        return miningSpeed*.7f;
    }

    public boolean isTreasure() {
        return false;
    }

    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }

    public boolean testOwnerFaithfulness(ItemStack stack, LivingEntity owner){
        if(owner.world.isClient()){
            return true;
        }
        UUID id = loadItemUUID(stack);

        if(owner.hasStatusEffect(SBStatusEffects.POLYGAMY)){
            PolygamyInstance polygamy = (PolygamyInstance)(owner.getStatusEffect(SBStatusEffects.POLYGAMY));
            owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
            if(polygamy.itemUUID.compareTo(id) != 0){
                polygamy = new PolygamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true);
                polygamy.itemUUID = id;
                owner.applyStatusEffect(polygamy);
            }
            return false;
        }

        if(owner.hasStatusEffect(SBStatusEffects.MONOGAMY)) {
            MonogamyInstance monogamy = (MonogamyInstance)(owner.getStatusEffect(SBStatusEffects.MONOGAMY));
            if(monogamy.itemUUID.compareTo(id) != 0){
                owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
                owner.applyStatusEffect(new PolygamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true));
                return false;
            }
        }
        //owner.removeStatusEffect(SBStatusEffects.MONOGAMY);
        owner.applyStatusEffect(new MonogamyInstance(id, SBConfig.INTIMACY_DURATION,0,false,false,true));
        return true;
    }

    private UUID loadItemUUID(ItemStack stack){
        CompoundTag tag = stack.getOrCreateTag();
        UUID id;
        if(tag.contains(Spellbound.MODID+"ItemID")){
            id = tag.getUuid(Spellbound.MODID+"ItemID");
        }
        else{
            id = UUID.randomUUID();
            tag.putUuid(Spellbound.MODID+"ItemID",id);
        }
        return id;
    }

}
