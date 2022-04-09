package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.ninbot.common.command.AutoCompleteCommand;
import dev.nincodedo.ninbot.common.command.slash.SlashCommand;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import dev.nincodedo.ninbot.common.message.SlashCommandEventMessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.Timestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class TimeCommand implements SlashCommand, AutoCompleteCommand {

    private Map<String, TimeFormat> timeFormatMap = Map.of(
            "relative", TimeFormat.RELATIVE,
            "time", TimeFormat.TIME_SHORT,
            "date", TimeFormat.DATE_SHORT,
            "datetime", TimeFormat.DATE_TIME_SHORT
    );

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
            case DISPLAY -> new ArrayList<>(timeFormatMap.keySet());
            default -> throw new IllegalStateException(String.format("TimeCommand autoComplete found option %s, which "
                    + "cannot be handled.", option));
        };
        commandAutoCompleteInteractionEvent.replyChoiceStrings(choices).queue();
    }

    @Override
    public MessageExecutor<SlashCommandEventMessageExecutor> executeCommandAction(@NotNull SlashCommandInteractionEvent slashCommandEvent) {
        SlashCommandEventMessageExecutor messageExecutor =
                new SlashCommandEventMessageExecutor(slashCommandEvent);
        Double amount = slashCommandEvent.getOption(TimeCommandName.Option.AMOUNT.get(), OptionMapping::getAsDouble);
        String unit = slashCommandEvent.getOption(TimeCommandName.Option.UNIT.get(), "days",
                OptionMapping::getAsString);
        String display = slashCommandEvent.getOption(TimeCommandName.Option.DISPLAY.get(), "relative",
                OptionMapping::getAsString);
        if (amount != null && unit != null && display != null) {
            Timestamp timestamp = getTimestamp(amount, unit, display);
            messageExecutor.addMessageResponse(timestamp.toString());
        } else {
            messageExecutor.addEphemeralMessage("how did you do this");
        }
        return messageExecutor;
    }

    @NotNull
    protected Timestamp getTimestamp(Double amount, String unit, String display) {
        ChronoUnit chronoUnit = Enum.valueOf(ChronoUnit.class, unit.toUpperCase());
        return now(timeFormatMap.get(display)).plus(Duration.of(amount.longValue(), chronoUnit));
    }

    protected Timestamp now(TimeFormat timeFormat) {
        return timeFormat.now();
    }

    private TimeCommandName.Option getOptionFromName(String name) {
        return TimeCommandName.Option.valueOf(name.toUpperCase());
    }

    @Override
    public List<OptionData> getCommandOptions() {
        return Arrays.asList(
                new OptionData(OptionType.NUMBER, TimeCommandName.Option.AMOUNT.get(), "The quantity of time.", true).setMinValue(0),
                new OptionData(OptionType.STRING, TimeCommandName.Option.UNIT.get(), "The unit of time. Defaults to "
                        + "days.", false, true),
                new OptionData(OptionType.STRING, TimeCommandName.Option.DISPLAY.get(),
                        "How the timestamp will be displayed. Defaults to relative.", false, true)
        );
    }
}
