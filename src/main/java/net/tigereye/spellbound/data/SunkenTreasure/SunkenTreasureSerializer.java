package net.tigereye.spellbound.data.SunkenTreasure;

import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SunkenTreasureSerializer {
    public Map<Identifier,SunkenTreasureData> read(Identifier id, SunkenTreasureJsonFormat sunkenTreasureJson) {

        if (sunkenTreasureJson.lootTables == null) {
            throw new JsonSyntaxException("Sunken Treasure entry" + id + " must provide a loot table");
        }
        if (sunkenTreasureJson.quality < 0) {
            throw new JsonSyntaxException("Sunken Treasure entry" + id + " cannot have less than 0 quality");
        }
        if (sunkenTreasureJson.weight <= 0) {
            throw new JsonSyntaxException("Sunken Treasure entry" + id + " must have positive weight");
        }

        Set<Identifier> dimensionList = new HashSet<>();
        if(sunkenTreasureJson.dimensionList != null) {
            int i = 0;
            for (JsonElement entry :
                    sunkenTreasureJson.dimensionList) {
                ++i;
                try {
                    if(!dimensionList.add(new Identifier(entry.getAsString()))){
                        Spellbound.LOGGER.warn("Sunken Treasure entry "+id+": Duplicate dimension identifier no. " + i);
                    }
                } catch (Exception e) {
                    Spellbound.LOGGER.error("Sunken Treasure entry "+id+": Error parsing dimension identifier no. " + i);
                }
            }
        }

        Map<Identifier, SunkenTreasureData> treasureMap = new HashMap<>();
        int i = 0;
        for (JsonElement entry: sunkenTreasureJson.lootTables){
            ++i;
            SunkenTreasureData sunkenTreasureData = new SunkenTreasureData();
            sunkenTreasureData.quality = sunkenTreasureJson.quality;
            sunkenTreasureData.weight = sunkenTreasureJson.weight;
            sunkenTreasureData.dimensionList = new HashSet<>(dimensionList);
            sunkenTreasureData.isWhiteList = sunkenTreasureJson.isWhiteList;
            sunkenTreasureData.replace = sunkenTreasureJson.replace;

            try {
                treasureMap.put(new Identifier(entry.getAsString()),sunkenTreasureData);
            } catch (Exception e) {
                Spellbound.LOGGER.error("Sunken Treasure entry "+id+": Error parsing lootTable identifier no. " + i);
            }
        }

        return treasureMap;
    }
}
