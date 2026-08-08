package net.tigereye.spellbound.components;

import java.util.ArrayList;
import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record VengenceComponent(List<Entry> grudges) {
    
   public static final TrophyCollectionComponent EMPTY = new TrophyCollectionComponent(List.of());
   public static final Codec<VengenceComponent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, VengenceComponent> STREAM_CODEC;

    static {
        CODEC = VengenceComponent.Entry.CODEC.listOf().xmap(VengenceComponent::new, VengenceComponent::grudges);
        STREAM_CODEC = VengenceComponent.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(VengenceComponent::new, VengenceComponent::grudges);
    }

    public static VengenceComponent ofGrudge(String UUID, float amount) {
        List<Entry> grudges = new ArrayList<>();
        grudges.add(new Entry(UUID, amount));
        return new VengenceComponent(grudges);
    }

    public VengenceComponent withAddedGrudge(String UUID, float injury){
        List<Entry> copy = List.of();
        boolean hasGrudgeAlready = false;
        //this isn't gonna work, need to remove the old entry
        for(Entry entry : grudges){
            if(entry.UUID == UUID){
                copy.add(new Entry(entry.UUID,entry.injury + injury));
                hasGrudgeAlready = true;
            }
            else{
                copy.add(entry);
            }
        };
        if(!hasGrudgeAlready){
            copy.add(new Entry(UUID, injury));
        }
        return new VengenceComponent(copy);
    }

    public VengenceComponent withRemovedGrudge(String UUID){
        List<Entry> copy = List.of();
        for(Entry entry : grudges){
            if(entry.UUID != UUID){
                copy.add(entry);
            }
        };
        return new VengenceComponent(copy);
    }

    public float getGrudge(String UUID){
        for(Entry grudge : grudges){
            if(grudge.UUID.equals(UUID)){
                return grudge.injury;
            }
        }
        return 0;
    }

    public static record Entry(String UUID, float injury) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(Codec.STRING.fieldOf("UUID").forGetter(Entry::UUID),
            Codec.FLOAT.lenientOptionalFieldOf("injury", 0f).forGetter(Entry::injury)).apply(instance, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

        static {
            STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.STRING_UTF8, Entry::UUID, ByteBufCodecs.FLOAT, Entry::injury, Entry::new);
        }
    }
}
