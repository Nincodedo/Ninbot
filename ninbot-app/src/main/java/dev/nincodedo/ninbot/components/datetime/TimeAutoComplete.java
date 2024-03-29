package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.nincord.command.AutoCompleteCommand;
import dev.nincodedo.nincord.message.AutoCompleteCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TimeAutoComplete implements AutoCompleteCommand {

    @Override
    public String getName() {
        return TimeCommandName.TIME.get();
    }

    @Override
    public MessageExecutor execute(@NotNull CommandAutoCompleteInteractionEvent event) {
        TimeCommandName.Option option = getOptionFromName(event.getFocusedOption()
                .getName());
        List<String> choices = switch (option) {
            case UNIT -> Arrays.asList("minutes", "hours", "seconds", "days");
            case DISPLAY -> new ArrayList<>(TimeCommon.timeFormatMap.keySet());
            default ->
                    throw new IllegalStateException(("TimeCommand autoComplete found option %s, which cannot be "
                            + "handled.").formatted(option));
        };
        event.replyChoiceStrings(choices).queue();
        return new AutoCompleteCommandMessageExecutor(event);
    }

    private TimeCommandName.Option getOptionFromName(String name) {
        return TimeCommandName.Option.valueOf(name.toUpperCase());
    }
}
