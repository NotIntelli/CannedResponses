package net.badbird5907.cannedresponses.object;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.cannedresponses.CannedResponses;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
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
    private List<String> keywords, //has to contain at least one keyword
            keywordsRequired; // has to contain all keywords
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

    public JsonObject asJsonObject() {
        JsonObject json = new JsonObject();
        json.addProperty("name", name);
        json.addProperty("response", response);
        json.add("keys", CannedResponses.getInstance().getGson().toJsonTree(keys));
        if (automated) {
            JsonObject automated = new JsonObject();
            automated.add("keywords", CannedResponses.getInstance().getGson().toJsonTree(keywords));
            automated.add("keywords-required", CannedResponses.getInstance().getGson().toJsonTree(keywordsRequired));
            if (minimumWords != -1)
                automated.addProperty("minimum-words", minimumWords);
            json.add("automated", automated);
        }
        return json;
    }

    public MessageEmbed getInfo() {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("Canned Message Info")
                .addField("Name", name, true)
                .addField("Response", response, true)
                .addField("Keys", keys.toString(), true);
        if (automated) {
            builder.addField("Automated", "Yes", true)
                    .addField("Keywords", keywords.toString(), true)
                    .addField("Keywords Required", keywordsRequired.toString(), true)
                    .addField("Minimum Words To Activate", (minimumWords != -1 ? minimumWords : CannedResponses.getInstance().getConfigManager().getMinimumWords()) + "", true);
        }
        return builder.build();
    }
}
