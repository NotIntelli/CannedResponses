package net.badbird5907.cannedresponses.listener;

import net.badbird5907.cannedresponses.CannedResponses;
import net.badbird5907.cannedresponses.object.CannedMessage;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
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

        boolean cannedResponse = content.startsWith(bot.getConfigManager().getPrefix());
        for (CannedMessage cannedMessage : bot.getConfigManager().getMessageConfig().getCannedMessages()) {
            if (cannedResponse)
                if (cannedMessage.getKeys().stream().anyMatch(k -> content.replace(bot.getConfigManager().getPrefix(), "").equalsIgnoreCase(k))) {
                    event.getMessage().reply(cannedMessage.getResponse()).queue();
                    return;
                }
            if (cannedMessage.isAutomated()) {
                if (!cannedMessage.getKeywordsRequired().isEmpty() && cannedMessage.getKeywordsRequired().stream().noneMatch(keyword -> content.toLowerCase().contains(keyword.toLowerCase()))) {
                    continue;
                }
                if (!cannedMessage.getKeywords().isEmpty() && cannedMessage.getKeywords().stream().noneMatch(keyword -> content.toLowerCase().contains(keyword.toLowerCase()))) {
                    continue;
                }

                if (cannedMessage.canReply(content, words, bot)) {
                    event.getMessage().reply(cannedMessage.getResponse()).queue();
                    return;
                }
            }
        }
    }
}
