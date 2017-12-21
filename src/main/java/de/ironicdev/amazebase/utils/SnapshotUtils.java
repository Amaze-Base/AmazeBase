package de.ironicdev.amazebase.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.util.JSON;
import de.ironicdev.amazebase.models.Snapshot;

import java.util.Map;

/**
 * Created by marco on 05.12.17.
 */
public class SnapshotUtils {
    public static JsonObject toJson(Snapshot snapshot) {
        JsonObject jsonObj = new JsonObject();

        JsonObject json = new JsonParser().parse(snapshot.getData()).getAsJsonObject();

        for (Map.Entry<String, JsonElement> ENTRY : json.entrySet()) {
            if (!ENTRY.getKey().equals("_nodeId")) jsonObj.add(ENTRY.getKey(), ENTRY.getValue());
        }

        for (Snapshot CHILD : snapshot.getChildren()) {
            JsonObject childJson = new JsonParser().parse(CHILD.getData()).getAsJsonObject();

            JsonElement nodeId = childJson.get("_nodeId");

            if (nodeId == null) {
                if (CHILD.getNodeName().equals("")) {
                    for (Map.Entry<String, JsonElement> ENTRY : childJson.entrySet()) {
                        jsonObj.add(ENTRY.getKey(), ENTRY.getValue());
                    }
                } else {
                    jsonObj.add(CHILD.getNodeName(), toJson(CHILD));
                }
            } else {
                jsonObj.add(nodeId.getAsString(), toJson(CHILD));
            }

            jsonObj.remove("_nodeId"); // do not display system props
        }

        return jsonObj;
    }
}
