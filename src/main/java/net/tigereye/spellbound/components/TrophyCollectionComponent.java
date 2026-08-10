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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

public record TrophyCollectionComponent (@NotNull List<Entry> trophies) {
   public static final TrophyCollectionComponent EMPTY = new TrophyCollectionComponent(List.of());
   public static final Codec<TrophyCollectionComponent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, TrophyCollectionComponent> STREAM_CODEC;

    public TrophyCollectionComponent withTrophyAdded(EntityType<?> trophy) {
        String trophyName = getEntityTypeName(trophy);
        List<Entry> copy = new ArrayList<>();
        boolean hasTrophyAlready = false;
        //this isn't gonna work, need to remove the old entry
        for(Entry entry : trophies){
            if(entry.entityType.equals(trophyName)){
                copy.add(new Entry(entry.entityType,entry.count+1));
                hasTrophyAlready = true;
            }
            else{
                copy.add(entry);
            }
        };
        if(!hasTrophyAlready){
            copy.add(new Entry(trophyName, 1));
        }
        return new TrophyCollectionComponent(copy);
    }

    public static TrophyCollectionComponent ofTrophy(EntityType<?> trophy) {
        List<Entry> trophies = new ArrayList<>();
        trophies.add(new Entry(getEntityTypeName(trophy), 1));
        return new TrophyCollectionComponent(trophies);
    }

    public static TrophyCollectionComponent ofMap(Map<String,Integer> trophyMap) {
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
            if(entry.entityType.equals(getEntityTypeName(trophy))) return true;
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
            if(entry.entityType.equals(getEntityTypeName(trophy))) return entry.count;
        }
        return 0;
    }

    private static String getEntityTypeName(EntityType<?> trophy){
        ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(trophy);
        return key.toString();
    }

    public static record Entry(String entityType, int count) {


        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(Codec.STRING.fieldOf("id").forGetter(Entry::entityType),
            Codec.INT.lenientOptionalFieldOf("count", 0).forGetter(Entry::count)).apply(instance, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

        static {
            STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Entry::entityType,
                    ByteBufCodecs.VAR_INT, Entry::count, Entry::new);
        }
    }
}
