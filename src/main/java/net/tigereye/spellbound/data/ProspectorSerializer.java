package net.tigereye.spellbound.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;

public class ProspectorSerializer {
    public ProspectorData read(Identifier id, ProspectorJsonFormat prospectorJson) {

        if (prospectorJson.treasure == null) {
            throw new JsonSyntaxException("Prospector entry" + id + " must provide treasure");
        }
        if (prospectorJson.frequency == 0) {
            throw new JsonSyntaxException("Prospector entry" + id + " cannot have 0 frequency");
        }


        ProspectorData prospectorData = new ProspectorData();
        prospectorData.treasure = new Identifier(prospectorJson.treasure);
        prospectorData.frequency = prospectorJson.frequency;

        prospectorData.bonusOre = new LinkedList<>();
        if (prospectorJson.bonusOre != null){
            for (JsonElement bonusOre : prospectorJson.bonusOre) {
                prospectorData.bonusOre.add(new Identifier(bonusOre.getAsString()));
            }
        }

        return prospectorData;
    }
}
