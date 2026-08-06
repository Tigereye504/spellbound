package net.tigereye.spellbound;

import com.chocohead.mm.api.ClassTinkerers;
import com.mojang.datafixers.DSL;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;

public class SpellboundEarlyRiser implements Runnable {


    public static final DSL.TypeReference SAVED_DATA_RESURFACING_REF = () -> "saved_data/spellbound/resurfacing";
    public static final DSL.TypeReference SAVED_DATA_TOUCHED_BLOCKS_REF = () -> "saved_data/spellbound/touched_blocks";

    @Override
    public void run() {
        MappingResolver remapper = FabricLoader.getInstance().getMappingResolver();

        final String dataFixTypesTarget = remapper.mapClassName(
                "intermediary",
                "net.minecraft.class_4284"
        );

        registerDataFixType(dataFixTypesTarget);
    }


    public static void registerDataFixType(String dataFixTypesTarget) {
        registerDataFixType(dataFixTypesTarget, "SAVED_DATA_RESURFACING",SAVED_DATA_RESURFACING_REF);
        registerDataFixType(dataFixTypesTarget, "SAVED_DATA_TOUCHED_BLOCKS",SAVED_DATA_TOUCHED_BLOCKS_REF);
    }

    private static void registerDataFixType(String dataFixTypesTarget, String name, DSL.TypeReference ref){
        ClassTinkerers.enumBuilder(dataFixTypesTarget,DSL.TypeReference.class).addEnum(
                name,ref).build();
    }
}
