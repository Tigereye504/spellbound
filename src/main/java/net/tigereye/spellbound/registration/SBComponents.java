package net.tigereye.spellbound.registration;

import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.tigereye.spellbound.blocks.entity.CrateBlockEntity;
import net.tigereye.spellbound.components.RockCollectionComponent;
import net.tigereye.spellbound.components.TrophyCollectionComponent;
import net.tigereye.spellbound.components.VengenceComponent;
import net.tigereye.spellbound.enchantments.damage.TrophyCollectingEnchantment;
import net.tigereye.spellbound.enchantments.efficiency.AccelerationEnchantment;
import net.tigereye.spellbound.enchantments.efficiency.RockCollectingEnchantment;
import net.tigereye.spellbound.enchantments.looting.PinataEnchantment;
import net.tigereye.spellbound.enchantments.meta.StoriedEnchantment;
import net.tigereye.spellbound.enchantments.retaliation.OutburstEnchantment;
import net.tigereye.spellbound.enchantments.retaliation.VengefulEnchantment;
import net.tigereye.spellbound.enchantments.unbreaking.BufferedEnchantment;

public class SBComponents {

    public static DataComponentType<Float> ACCELERATION_STACKS;
    public static DataComponentType<Long> ACCELERATION_TIME;

    public static DataComponentType<Long> BUFFERED_TIME;

    public static DataComponentType<Integer> CRATE_QUALITY;
    public static DataComponentType<String> CRATE_DIMENSION;

    public static DataComponentType<RockCollectionComponent> ROCK_COLECTION;

    public static DataComponentType<Integer> OUTBURST_RAGE;

    public static DataComponentType<Integer> PINIATA_PROGRESS;

    public static DataComponentType<Float> STORIED_XP;

    public static DataComponentType<TrophyCollectionComponent> TROPHY_COLECTION;

    public static DataComponentType<VengenceComponent> VENGEFUL_GRUDGES;

    private static <T> DataComponentType<T> register(String string, UnaryOperator<DataComponentType.Builder<T>> unaryOperator) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, string, ((DataComponentType.Builder<T>)unaryOperator.apply(DataComponentType.builder())).build());
    }

    public static void register(){
        ACCELERATION_STACKS = register(AccelerationEnchantment.ACCELERATION_STACKS_KEY,
                (builder) -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));
        ACCELERATION_TIME = register(AccelerationEnchantment.ACCELERATION_TIME_KEY,
                (builder) -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
        BUFFERED_TIME = register(BufferedEnchantment.BUFFER_TIME_KEY,
                (builder) -> builder.persistent(Codec.LONG).networkSynchronized(ByteBufCodecs.VAR_LONG));
        CRATE_QUALITY = register(CrateBlockEntity.LOOT_QUALITY_KEY,
                (builder) -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
        CRATE_DIMENSION = register(CrateBlockEntity.LOOT_DIMENSION_KEY,
                (builder) -> builder.persistent(ExtraCodecs.ESCAPED_STRING).networkSynchronized(ByteBufCodecs.STRING_UTF8));
        ROCK_COLECTION = register(RockCollectingEnchantment.ROCK_COLLECTOR_KEY,
                (builder) -> builder.persistent(RockCollectionComponent.CODEC).networkSynchronized(RockCollectionComponent.STREAM_CODEC).cacheEncoding());
        OUTBURST_RAGE = register(OutburstEnchantment.OUTBURST_RAGE_KEY,
                (builder) -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
        PINIATA_PROGRESS = register(PinataEnchantment.PINATA_KILL_COUNT_KEY,
                (builder) -> builder.persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
        STORIED_XP = register(StoriedEnchantment.STORIED_XP_KEY,
                (builder) -> builder.persistent(Codec.FLOAT).networkSynchronized(ByteBufCodecs.FLOAT));
        TROPHY_COLECTION = register(TrophyCollectingEnchantment.TROPHY_COLLECTOR_KEY,
                (builder) -> builder.persistent(TrophyCollectionComponent.CODEC).networkSynchronized(TrophyCollectionComponent.STREAM_CODEC).cacheEncoding());
        VENGEFUL_GRUDGES = register(VengefulEnchantment.VENGENCE_KEY,
                (builder) -> builder.persistent(VengenceComponent.CODEC).networkSynchronized(VengenceComponent.STREAM_CODEC).cacheEncoding());
    }
}
