package net.tigereye.spellbound.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.EntityType;
public record TrophyCollectionComponent (List<Entry> trophies) {
   public static final TrophyCollectionComponent EMPTY = new TrophyCollectionComponent(List.of());
   public static final Codec<TrophyCollectionComponent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, TrophyCollectionComponent> STREAM_CODEC;

    public TrophyCollectionComponent withTrophyAdded(Holder<EntityType<?>> trophy) {
        List<Entry> copy = List.of();
        boolean hasRockAlready = false;
        //this isn't gonna work, need to remove the old entry
        for(Entry entry : trophies){
            if(entry.entityType.value() == trophy.value()){
                copy.add(new Entry(entry.entityType,entry.count+1));
                hasRockAlready = true;
            }
            else{
                copy.add(entry);
            }
        };
        if(!hasRockAlready){
            copy.add(new Entry(trophy, 1));
        }
        return new TrophyCollectionComponent(copy);
    }
    public TrophyCollectionComponent withTrophyAdded(EntityType<?> trophy) {
        return withTrophyAdded(Holder.direct(trophy));
    }

    public static TrophyCollectionComponent ofTrophy(Holder<EntityType<?>> trophy) {
        List<Entry> rocks = new ArrayList<>();
        rocks.add(new Entry(trophy, 1));
        return new TrophyCollectionComponent(rocks);
    }
    public static TrophyCollectionComponent ofTrophy(EntityType<?> trophy) {
        return ofTrophy(Holder.direct(trophy));
    }

    public static TrophyCollectionComponent ofMap(Map<Holder<EntityType<?>>,Integer> trophyMap) {
        List<Entry> rocks = new ArrayList<>();
        trophyMap.forEach((trophy,count) -> rocks.add(new Entry(trophy, count)));
        return new TrophyCollectionComponent(rocks);
    }

    static {
        CODEC = TrophyCollectionComponent.Entry.CODEC.listOf().xmap(TrophyCollectionComponent::new, TrophyCollectionComponent::trophies);
        STREAM_CODEC = TrophyCollectionComponent.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(TrophyCollectionComponent::new, TrophyCollectionComponent::trophies);
    }

    public boolean hasTrophy(EntityType<?> trophy){
        for(Entry entry : trophies){
            if(entry.entityType.value() == trophy) return true;
        }
        return false;
    }
    public boolean hasTrophy(Holder<EntityType<?>> trophy){
        return hasTrophy(trophy.value());
    }

    public int getTrophyCopies(Holder<EntityType<?>> trophy){
        return getTrophyCopies(trophy.value());
    }
    public int getTrophyCopies(EntityType<?> trophy){
        for(Entry entry : trophies){
            if(entry.entityType.value() == trophy) return entry.count;
        }
        return 0;
    }

    public static record Entry(Holder<EntityType<?>> entityType, int count) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(BuiltInRegistries.ENTITY_TYPE.holderByNameCodec().fieldOf("id").forGetter(Entry::entityType),
            Codec.INT.lenientOptionalFieldOf("count", 0).forGetter(Entry::count)).apply(instance, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

        static {
            STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE), Entry::entityType, ByteBufCodecs.VAR_INT, Entry::count, Entry::new);
        }
    }
}
