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
    @Command(name = "cannedinfo",description = "Shows information about a canned message")
    public void info(CommandContext ctx, @Required String key) {
        CannedMessage canned = CannedResponses.getInstance().getConfigManager().getCannedMessage(key);
        if (canned == null) {
            ctx.reply("No message found with that key");
            return;
        }
        ctx.reply(canned.getInfo());
    }

    @Command(name = "reloadcanned", description = "Reload config (canned messages)")
    public void reload(CommandContext ctx) {
        if (!ModifyCommand.canModify(ctx)) {
            ctx.reply("Imagine not having permissions L");
            return;
        }
        long start = System.currentTimeMillis();
        CannedResponses.getInstance().getConfigManager().reload(CannedResponses.getInstance());
        ctx.reply("Reloaded canned messages config in " + (System.currentTimeMillis() - start) + "ms");
    }

}
