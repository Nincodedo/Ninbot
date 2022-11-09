package dev.nincodedo.ninbot.common.command.slash;

import dev.nincodedo.ninbot.common.command.Command;
import dev.nincodedo.ninbot.common.command.CommandType;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

public interface SlashCommand extends Command<SlashCommandInteractionEvent> {

    @Override
    default CommandType getType() {
        return CommandType.SLASH;
    }

    default String getDescription() {
        return getDescription(defaultLocale);
    }

    default String getDescription(Locale locale) {
        return resourceBundle(locale).getString("command." + getName() + ".description");
    }

    default List<OptionData> getCommandOptions() {
        return Collections.emptyList();
    }

    default List<SubcommandData> getSubcommandDatas() {
        return Collections.emptyList();
    }

    @Override
    default MessageExecutor execute(@NotNull SlashCommandInteractionEvent event) {
        var messageExecutor = new SlashCommandEventMessageExecutor(event);
        return execute(event, messageExecutor);
    }

    MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor);
}
