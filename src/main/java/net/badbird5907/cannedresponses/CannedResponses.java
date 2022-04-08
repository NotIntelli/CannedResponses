package net.badbird5907.cannedresponses;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import net.badbird5907.cannedresponses.commands.CannedMessagesCommand;
import net.badbird5907.cannedresponses.listener.MessageListener;
import net.badbird5907.cannedresponses.manager.ConfigManager;
import net.badbird5907.cannedresponses.util.EnvConfig;
import net.badbird5907.jdacommand.JDACommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.Date;
import java.util.Scanner;

public class CannedResponses {
    @Getter
    private static CannedResponses instance;
    @Getter
    private JDA jda;
    @Getter
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Getter
    private boolean enabled = false;
    @Getter
    private ConfigManager configManager;
    public static void main(String[] args) {
        new CannedResponses();
    }
    public CannedResponses(){
        instance = this;
        String token = new EnvConfig().getConfigs().get("token");
        try {
            jda = JDABuilder.createDefault(token)
                    .enableIntents(GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new MessageListener(this))
                    .build();

            configManager = new ConfigManager(this);

            jda.awaitReady();
            //set bot status
            jda.getPresence().setPresence(OnlineStatus.DO_NOT_DISTURB, Activity.of(Activity.ActivityType.WATCHING,"Your Messages"),false);
            JDACommand command = new JDACommand(jda); // Our command framework - https://github.com/Badbird-5907/JDACommand
            command.registerCommand(new CannedMessagesCommand()); // Register commands in package

            System.out.println("Canned Responses Bot Successfully connected to " + jda.getSelfUser().getAsTag() + " (" + jda.getSelfUser().getIdLong() + ") " + new Date());
            System.out.println("Registering commands with discord, this may take a while...");
            enabled = true;

            if (System.getProperty("cannedresponses.dev").equalsIgnoreCase("true")) {
                new Thread("Console Thread") { // to gracefully shutdown if using intellij
                    @Override
                    public void run() {
                        Scanner scanner = new Scanner(System.in);
                        while (scanner.hasNextLine()) {
                            String line = scanner.nextLine();
                            if (line.equalsIgnoreCase("exit")) {
                                System.exit(0);
                            }
                        }
                    }
                }.start();
            }
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
