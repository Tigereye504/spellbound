package net.tigereye.spellbound.registration;

import com.chocohead.mm.api.ClassTinkerers;

import net.minecraft.util.datafix.DataFixTypes;

public class SBSaveData {
    public static DataFixTypes SAVED_DATA_RESURFACING = ClassTinkerers.getEnum(DataFixTypes.class, "SAVED_DATA_RESURFACING");
    public static DataFixTypes SAVED_DATA_TOUCHED_BLOCKS = ClassTinkerers.getEnum(DataFixTypes.class, "SAVED_DATA_TOUCHED_BLOCKS");

}
