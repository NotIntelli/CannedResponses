package net.badbird5907.cannedresponses.commands;

import net.badbird5907.cannedresponses.CannedResponses;
import net.badbird5907.cannedresponses.object.CannedMessage;
import net.badbird5907.jdacommand.annotation.Command;
import net.badbird5907.jdacommand.annotation.Optional;
import net.badbird5907.jdacommand.context.CommandContext;

public class CannedMessagesCommand {
    @Command(name = "cannedmessages", aliases = {"canned"}, description = "Shows a list of canned messages")
    public void cannedMessages(CommandContext ctx, @Optional String key) {
        if (key == null) {
            ctx.reply(CannedResponses.getInstance().getConfigManager().getMessageConfig().getEmbed());
        }else {
            for (CannedMessage cannedMessage : CannedResponses.getInstance().getConfigManager().getMessageConfig().getCannedMessages()) {
                if (cannedMessage.getKeys().stream().anyMatch(key::equalsIgnoreCase)) {
                    ctx.reply(cannedMessage.getResponse());
                    return;
                }
            }

            ctx.reply("No message found with that key");
        }
    }
}
