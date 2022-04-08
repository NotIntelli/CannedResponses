package net.badbird5907.cannedresponses;

import net.badbird5907.cannedresponses.util.EnvConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

import javax.security.auth.login.LoginException;

public class CannedResponses {
    private static JDA jda;
    public static void main(String[] args) {
        String token = new EnvConfig().getConfigs().get("token");
        try {
            jda = JDABuilder.createDefault(token).build();

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }
}
