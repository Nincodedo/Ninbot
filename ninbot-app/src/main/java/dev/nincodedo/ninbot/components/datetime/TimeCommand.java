package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.nincord.command.slash.SlashCommand;
import dev.nincodedo.nincord.message.MessageExecutor;
import dev.nincodedo.nincord.message.SlashCommandEventMessageExecutor;
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
import java.util.Arrays;
import java.util.List;

@Component
public class TimeCommand implements SlashCommand {

    @Override
    public String getName() {
        return TimeCommandName.TIME.get();
    }

    @Override
    public MessageExecutor execute(@NotNull SlashCommandInteractionEvent event,
            @NotNull SlashCommandEventMessageExecutor messageExecutor) {
        Double amount = event.getOption(TimeCommandName.Option.AMOUNT.get(), OptionMapping::getAsDouble);
        String unit = event.getOption(TimeCommandName.Option.UNIT.get(), "days",
                OptionMapping::getAsString);
        String display = event.getOption(TimeCommandName.Option.DISPLAY.get(), "relative",
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
        return now(TimeCommon.timeFormatMap.get(display)).plus(Duration.of(amount.longValue(), chronoUnit));
    }

    protected Timestamp now(TimeFormat timeFormat) {
        return timeFormat.now();
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
