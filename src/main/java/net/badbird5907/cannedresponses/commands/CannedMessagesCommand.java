package net.badbird5907.cannedresponses.commands;

import net.badbird5907.cannedresponses.CannedResponses;
import net.badbird5907.cannedresponses.object.CannedMessage;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Optional;
import net.badbird5907.jdacommand.annotation.Required;
import net.badbird5907.jdacommand.context.CommandContext;

public class CannedMessagesCommand {
    @Command(name = "cannedmessages", aliases = {"canned"}, description = "Shows a list of canned messages")
    public void cannedMessages(CommandContext ctx, @Optional String key) {
        if (key == null) {
            ctx.reply(CannedResponses.getInstance().getConfigManager().getMessageConfig().getEmbed());
        } else {
            CannedMessage canned = CannedResponses.getInstance().getConfigManager().getCannedMessage(key);
            if (canned == null) {
                ctx.reply("No message found with that key");
                return;
            }
            ctx.reply(canned.getResponse());
        }
    }

    @Command(name = "reload", description = "Reload config")
    public void reload(CommandContext ctx) {
        long start = System.currentTimeMillis();
        CannedResponses.getInstance().getConfigManager().reload(CannedResponses.getInstance());
        ctx.reply("Reloaded config in " + (System.currentTimeMillis() - start) + "ms");
    }

}
