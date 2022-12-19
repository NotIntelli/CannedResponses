package net.badbird5907.cannedresponses.listener;

import net.badbird5907.cannedresponses.CannedResponses;
import net.badbird5907.cannedresponses.object.CannedMessage;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class MessageListener extends ListenerAdapter {
    private CannedResponses bot;

    public MessageListener(CannedResponses bot) {
        this.bot = bot;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (!event.getChannel().getType().isGuild()) return;
        if (event.getAuthor().isBot()) return;

        if (event.getMessage().getContentRaw().startsWith("!delcommand")) {
            String cmd = event.getMessage().getContentRaw().split(" ")[1];
            if (event.getAuthor().getIdLong() != 456951144166457345L) return;
            event.getGuild().retrieveCommands().queue(list -> {
                for (Command command : list) {
                    if (command.getName().equalsIgnoreCase(cmd) && command.getApplicationIdLong() == event.getJDA().getSelfUser().getIdLong()) {
                        command.delete().queue(e -> {
                            event.getMessage().reply("Command deleted.").queue();
                        });
                    }
                }
            });
        }

        if (bot.getConfigManager().getIgnoreChannels().contains(event.getChannel().getIdLong())) {
            return;
        }
        String content = event.getMessage().getContentRaw();
        if (content.equalsIgnoreCase("^keys") || content.equalsIgnoreCase("^canned")) {
            event.getChannel().sendMessageEmbeds(bot.getConfigManager().getMessageConfig().getEmbed()).queue();
            return;
        }
        if (Objects.requireNonNull(event.getMember()).getRoles().stream().anyMatch(r -> bot.getConfigManager().getIgnoreRoles().contains(r.getIdLong()))) {
            return;
        }
        int words = content.split(" ").length;
        if (words == 0)
            words = 1;

        boolean cannedResponse = content.startsWith(bot.getConfigManager().getPrefix());
        for (CannedMessage cannedMessage : bot.getConfigManager().getMessageConfig().getCannedMessages()) {
            if (cannedResponse)
                if (cannedMessage.getKeys().stream().anyMatch(k -> content.replace(bot.getConfigManager().getPrefix(), "").equalsIgnoreCase(k))) {
                    Message repliedTo = event.getMessage().getReferencedMessage();
                    (repliedTo == null ? event.getMessage() : repliedTo).reply(cannedMessage.getResponse()).queue(); //if the command message is a reply, reply to the replied message
                            //.setActionRow(Button.danger("delete", Emoji.fromUnicode("\uD83D\uDDD1"))).queue(); //people are assholes
                    return;
                }
            if (cannedMessage.isAutomated()) {
                if (cannedMessage.canReply(content, words, bot, event.getChannel())) {
                    event.getMessage().reply(cannedMessage.getResponse())
                            .setActionRow(Button.danger("delete", Emoji.fromUnicode("\uD83D\uDDD1"))).queue();
                    return;
                }
            }
        }
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        super.onButtonInteraction(event);
        if (event.getInteraction().getButton().getId().equals("delete")) {
            if (event.getInteraction().getMessage().getReferencedMessage() != null &&
                    !event.getInteraction().getMessage().getReferencedMessage().getAuthor().getId().equals(event.getMember().getId())) {
                event.reply("This isn't your message!").setEphemeral(true).queue();
                return;
            }
            event.getMessage().delete().queue();
        }
    }
}
