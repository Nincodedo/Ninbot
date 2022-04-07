package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.ninbot.common.command.AutoCompleteCommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class TimeCommand implements SlashCommand, AutoCompleteCommand {
    @Override
    public String getName() {
        return TimeCommandName.TIME.get();
    }

    @Override
    public void autoComplete(CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        TimeCommandName.Option option = getOptionFromName(commandAutoCompleteInteractionEvent.getFocusedOption()
                .getName());
        List<String> choices = switch (option) {
            case UNIT -> Arrays.asList("minutes", "hours", "seconds", "days");
            case DISPLAY -> Arrays.asList("relative", "time", "datetime");
            default ->
                    throw new IllegalStateException(String.format("TimeCommand autoComplete found option %s, which "
                            + "cannot be handled.", option));
        };
        commandAutoCompleteInteractionEvent.replyChoiceStrings(choices).queue();
    }

    private TimeCommandName.Option getOptionFromName(String name) {
        return TimeCommandName.Option.valueOf(name.toUpperCase());
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(
                new OptionData(OptionType.INTEGER, TimeCommandName.Option.AMOUNT.get(), "The quantity of time.", true),
                new OptionData(OptionType.STRING, TimeCommandName.Option.UNIT.get(), "The unit of time. Defaults to "
                        + "days.", false, true),
                new OptionData(OptionType.STRING, TimeCommandName.Option.DISPLAY.get(),
                        "How the timestamp will be displayed. Defaults to relative.", false, true)
        );
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        MessageExecutor<SlashCommandEventMessageExecutor> messageExecutor =
                new SlashCommandEventMessageExecutor(slashCommandEvent);

        return messageExecutor;
    }
}
