package net.badbird5907.cannedresponses.object;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.cannedresponses.CannedResponses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CannedMessage {
    private Set<String> keys;
    private String response = null;
    private boolean automated = false;
    private List<String> keywords,keywordsRequired;
    private int minimumWords = -1;
    private String name;

    public CannedMessage() {
        keywords = new ArrayList<>();
        keywordsRequired = new ArrayList<>();
        keys = new HashSet<>();
    }

    public CannedMessage load(JsonObject json) {
        json.get("keys").getAsJsonArray().forEach(key -> keys.add(key.getAsString().toLowerCase()));
        response = json.get("response").getAsString();
        name = json.get("name").getAsString();
        if (json.has("automated")) {
            automated = true;
            JsonObject automated = json.get("automated").getAsJsonObject();
            automated.get("keywords").getAsJsonArray().forEach(key -> keywords.add(key.getAsString().toLowerCase()));
            automated.get("keywords-required").getAsJsonArray().forEach(key -> keywordsRequired.add(key.getAsString().toLowerCase()));
            if (automated.has("minimum-words")) {
                minimumWords = automated.get("minimum-words").getAsInt();
            }
        }
        return this;
    }
    public boolean canReply(String message, int words, CannedResponses bot) {
        if (minimumWords != -1)
            return words >= minimumWords;
        return words >= bot.getConfigManager().getMinimumWords();
    }

}
