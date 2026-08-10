package net.tigereye.spellbound.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
public record RockCollectionComponent (List<Entry> rocks) {
   public static final RockCollectionComponent EMPTY = new RockCollectionComponent(List.of());
   public static final Codec<RockCollectionComponent> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, RockCollectionComponent> STREAM_CODEC;

    public RockCollectionComponent withRockAdded(Holder<Block> rock) {
        List<Entry> copy = new ArrayList<>();
        boolean hasRockAlready = false;
        for(Entry entry : rocks){
            if(entry.block == rock){
                copy.add(new Entry(entry.block,entry.count+1));
                hasRockAlready = true;
            }
            else{
                copy.add(entry);
            }
        };
        if(!hasRockAlready){
            copy.add(new Entry(rock, 1));
        }
        return new RockCollectionComponent(copy);
    }

    public static RockCollectionComponent ofRock(Holder<Block> rock) {
        List<Entry> rocks = new ArrayList<>();
        rocks.add(new Entry(rock, 1));
        return new RockCollectionComponent(rocks);
    }

    public static RockCollectionComponent ofMap(Map<Holder<Block>,Integer> rockMap) {
        List<Entry> rocks = new ArrayList<>();
        rockMap.forEach((block,count) -> rocks.add(new Entry(block, count)));
        return new RockCollectionComponent(rocks);
    }

    static {
        CODEC = RockCollectionComponent.Entry.CODEC.listOf().xmap(RockCollectionComponent::new, RockCollectionComponent::rocks);
        STREAM_CODEC = RockCollectionComponent.Entry.STREAM_CODEC.apply(ByteBufCodecs.list()).map(RockCollectionComponent::new, RockCollectionComponent::rocks);
    }

    public boolean hasRock(Holder<Block> block){
        for(Entry entry : rocks){
            if(entry.block == block) return true;
        }
        return false;
    }

    public int getRockCopies(Holder<Block> block){
        for(Entry entry : rocks){
            if(entry.block == block) return entry.count;
        }
        return 0;
    }

    public static record Entry(Holder<Block> block, int count) {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
            (instance) -> instance.group(BuiltInRegistries.BLOCK.holderByNameCodec().fieldOf("id").forGetter(Entry::block),
            Codec.INT.lenientOptionalFieldOf("count", 0).forGetter(Entry::count)).apply(instance, Entry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, Entry> STREAM_CODEC;

        static {
            STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.BLOCK), Entry::block, ByteBufCodecs.VAR_INT, Entry::count, Entry::new);
        }
    }
}
