package net.badbird5907.cannedresponses.provider;

import net.badbird5907.jdacommand.context.CommandContext;
import net.badbird5907.jdacommand.context.ParameterContext;
import net.badbird5907.jdacommand.provider.Provider;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class TextChannelProvider implements Provider<TextChannel> {
    @Override
    public TextChannel provide(CommandContext context, ParameterContext pContext) throws Exception {
        if (context.getOption(pContext.getName()) == null) {
            return provideDefault(context, pContext);
        }
        return context.getOption(pContext.getName()).getAsTextChannel();
    }

    @Override
    public TextChannel provideDefault(CommandContext context, ParameterContext pContext) {
        return null;
    }

    @Override
    public OptionData getOptionData(ParameterContext paramContext) {
        return new OptionData(OptionType.CHANNEL, paramContext.getArgName(), "Channel", paramContext.isRequired());
    }

    @Override
    public Class<?> getType() {
        return TextChannel.class;
    }

    @Override
    public Class<?>[] getExtraTypes() {
        return null;
    }

    @Override
    public boolean failOnException() {
        return true;
    }
}
