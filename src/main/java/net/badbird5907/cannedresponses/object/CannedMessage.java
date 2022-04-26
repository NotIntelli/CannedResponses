package net.badbird5907.cannedresponses.object;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.cannedresponses.CannedResponses;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Getter
@Setter
public class CannedMessage {
    private Set<String> keys;
    private String response = null;
    private boolean automated = false;
    private List<String> keywords, // has to contain at least one keyword
            keywordsRequired; // has to contain all keywords
    private int minimumWords = -1;
    private String name;
    private Set<Long> ignoredChannels;
    private transient Pattern requiredKeywordsPattern, keywordsPattern;

    public CannedMessage() {
        keywords = new ArrayList<>();
        keywordsRequired = new ArrayList<>();
        keys = new HashSet<>();
        ignoredChannels = new HashSet<>();
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
            if (automated.has("ignored-channels")) {
                json.get("ignored-channels").getAsJsonArray().forEach(channel -> ignoredChannels.add(channel.getAsLong()));
            }
            generateRegexPatterns();
        }
        return this;
    }

    public void generateRegexPatterns() {
        if (!keywordsRequired.isEmpty()) {
            StringBuilder regexBuilderRequired = new StringBuilder("(?i)(");
            for (String s : keywordsRequired) {
                regexBuilderRequired.append("\\b").append(s).append("\\b|");
            }
            regexBuilderRequired.deleteCharAt(regexBuilderRequired.length() - 1);
            regexBuilderRequired.append(")");
            System.out.println(name + " - required - " + regexBuilderRequired.toString());
            requiredKeywordsPattern = Pattern.compile(regexBuilderRequired.toString());
        } else {
            requiredKeywordsPattern = null;
        }
        if (!keywords.isEmpty()) {
            StringBuilder regexBuilder = new StringBuilder("(?i)(");
            for (String s : keywords) {
                regexBuilder.append("\\b").append(s).append("\\b|");
            }
            regexBuilder.deleteCharAt(regexBuilder.length() - 1);
            regexBuilder.append(")");
            System.out.println(name + " - " + regexBuilder);
            keywordsPattern = Pattern.compile(regexBuilder.toString());
        } else {
            keywordsPattern = null;
        }
    }

    public boolean canReply(String message, int words, CannedResponses bot, MessageChannel channel) {
        //if (!getKeywordsRequired().isEmpty() && getKeywordsRequired().stream().noneMatch(keyword -> message.toLowerCase().contains(keyword.toLowerCase()))) {
        if (!getKeywordsRequired().isEmpty() && requiredKeywordsPattern != null && getKeywordsRequired().stream().noneMatch(keyword -> requiredKeywordsPattern.matcher(message).find())) {
            return false;
        }
        //if (!getKeywords().isEmpty() && getKeywords().stream().noneMatch(keyword -> message.toLowerCase().contains(keyword.toLowerCase()))) {
        if (!getKeywords().isEmpty() && keywordsPattern != null && getKeywords().stream().noneMatch(keyword -> keywordsPattern.matcher(message).find())) {
            return false;
        }
        if (!ignoredChannels.isEmpty() && ignoredChannels.contains(channel.getIdLong())) {
            return false;
        }
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
            generateRegexPatterns();
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
            List<String> ignoredChannels = new ArrayList<>();
            for (Long ignoredChannel : getIgnoredChannels()) {
                ignoredChannels.add("<#" + ignoredChannel.toString() + ">");
            }
            builder.addField("Automated", "Yes", true)
                    .addField("Keywords", keywords.toString(), true)
                    .addField("Keywords Required", keywordsRequired.toString(), true)
                    .addField("Minimum Words To Activate", (minimumWords != -1 ? minimumWords : CannedResponses.getInstance().getConfigManager().getMinimumWords()) + "", true)
                    .addField("Ignored Channels", ignoredChannels.toString(), true)
            ;
            if (requiredKeywordsPattern != null) {
                builder.addField("Required Keywords Pattern", requiredKeywordsPattern.pattern(), true);
            }
            if (keywordsPattern != null) {
                builder.addField("Keywords Pattern", keywordsPattern.pattern(), true);
            }
        }
        return builder.build();
    }
}
