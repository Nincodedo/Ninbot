package dev.nincodedo.ninbot.components.datetime;

import dev.nincodedo.ninbot.common.command.AutoCompleteCommand;
import dev.nincodedo.ninbot.common.message.AutoCompleteCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
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
    public MessageExecutor<AutoCompleteCommandMessageExecutor> execute(@NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        TimeCommandName.Option option = getOptionFromName(commandAutoCompleteInteractionEvent.getFocusedOption()
                .getName());
        List<String> choices = switch (option) {
            case UNIT -> Arrays.asList("minutes", "hours", "seconds", "days");
            case DISPLAY -> new ArrayList<>(TimeCommon.timeFormatMap.keySet());
            default -> throw new IllegalStateException(String.format("TimeCommand autoComplete found option %s, which "
                    + "cannot be handled.", option));
        };
        commandAutoCompleteInteractionEvent.replyChoiceStrings(choices).queue();
        return new AutoCompleteCommandMessageExecutor(commandAutoCompleteInteractionEvent);
    }

    private TimeCommandName.Option getOptionFromName(String name) {
        return TimeCommandName.Option.valueOf(name.toUpperCase());
    }
}
