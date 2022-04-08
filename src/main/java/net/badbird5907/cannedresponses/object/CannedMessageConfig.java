package net.badbird5907.cannedresponses.object;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class CannedMessageConfig {
    @Getter
    private Set<CannedMessage> cannedMessages = new HashSet<>();

    public CannedMessageConfig(JsonArray arr) {
        for (JsonElement element : arr) {
            JsonObject obj = element.getAsJsonObject();
            cannedMessages.add(new CannedMessage().load(obj));
        }
    }

    public MessageEmbed getEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder
                .setColor(Color.YELLOW)
                .setTitle("Canned Responses")
                .setDescription("Canned Responses are \"responses in a can\" to help you answer questions quickly.");

        for (CannedMessage message : cannedMessages) {
            StringBuilder sb = new StringBuilder();
            for (String key : message.getKeys()) {
                sb.append(", `" + key + "`");
            }
            builder.addField(message.getName(), sb.substring(2), false);
        }
        return builder.build();
    }
}
