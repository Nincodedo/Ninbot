package dev.nincodedo.nincord.command.slash;

import dev.nincodedo.nincord.command.Subcommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;

public interface SlashSubCommand<T extends Enum<T>> extends SlashCommand, Subcommand<T> {
    default MessageExecutor execute(@NotNull SlashCommandInteractionEvent slashCommandEvent,
            @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        var subcommandName = slashCommandEvent.getSubcommandName();
        if (subcommandName == null) {
            return messageExecutor;
        }
        T subcommand = getSubcommand(subcommandName);
        return execute(slashCommandEvent, messageExecutor, subcommand);
    }

    MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor, @NotNull T subcommand);
}
