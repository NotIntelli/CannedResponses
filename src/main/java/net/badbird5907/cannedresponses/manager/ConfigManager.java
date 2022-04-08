package net.badbird5907.cannedresponses.manager;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.cannedresponses.CannedResponses;
import net.badbird5907.cannedresponses.object.CannedMessageConfig;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConfigManager {
    private static final File CONFIG_FILE = new File("config.json"),
            CANNED_MESSAGE_CONFIG = new File("canned_messages.json");

    @Getter
    private List<Long> managerRoles = new ArrayList<>(),
            ignoreRoles = new ArrayList<>(),
            ignoreChannels = new ArrayList<>();
    @Getter
    private String prefix;

    @Getter
    private int minimumWords = -1;

    @Getter
    private CannedMessageConfig messageConfig;

    public ConfigManager(CannedResponses bot) {
        reload(bot);
    }

    @SneakyThrows
    public void reload(CannedResponses bot) {
        if (!CONFIG_FILE.exists()) {
            //copy config.json in jar to config.json
            Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.json")), CONFIG_FILE.toPath());
        }
        if (!CANNED_MESSAGE_CONFIG.exists()) {
            //copy canned_messages.json in jar to canned_messages.json
            Files.copy(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("canned_messages.json")), CANNED_MESSAGE_CONFIG.toPath());
        }

        JsonObject configJson = JsonParser.parseString(new String(Files.readAllBytes(CONFIG_FILE.toPath()))).getAsJsonObject();
        for (JsonElement jsonElement : configJson.get("manager-roles").getAsJsonArray()) {
            managerRoles.add(jsonElement.getAsLong());
        }
        for (JsonElement jsonElement : configJson.get("ignore-roles").getAsJsonArray()) {
            ignoreRoles.add(jsonElement.getAsLong());
        }
        for (JsonElement jsonElement : configJson.get("ignore-channels").getAsJsonArray()) {
            ignoreChannels.add(jsonElement.getAsLong());
        }
        prefix = configJson.get("prefix").getAsString();

        if (configJson.has("automated-settings")) {
            JsonObject automatedSettings = configJson.get("automated-settings").getAsJsonObject();
            minimumWords = automatedSettings.get("minimum-words").getAsInt();
        }

        messageConfig = new CannedMessageConfig(JsonParser.parseString(new String(Files.readAllBytes(CANNED_MESSAGE_CONFIG.toPath()))).getAsJsonArray());
    }
}
