package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.ninbot.common.command.AutoCompleteCommand;
import dev.nincodedo.ninbot.common.command.Subcommand;
import dev.nincodedo.ninbot.common.message.AutoCompleteCommandMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CountdownAutoComplete implements AutoCompleteCommand, Subcommand<CountdownCommandName.Subcommand> {

    private CountdownRepository countdownRepository;

    public CountdownAutoComplete(CountdownRepository countdownRepository) {
        this.countdownRepository = countdownRepository;
    }

    @Override
    public String getName() {
        return CountdownCommandName.COUNTDOWN.get();
    }

    @Override
    public MessageExecutor<AutoCompleteCommandMessageExecutor> execute(@NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        var subcommandName = commandAutoCompleteInteractionEvent.getSubcommandName();
        var messageExecutor = new AutoCompleteCommandMessageExecutor(commandAutoCompleteInteractionEvent);
        if (subcommandName == null) {
            return messageExecutor;
        }
        if (getSubcommand(subcommandName) == CountdownCommandName.Subcommand.DELETE) {
            replyWithDeletableCountdowns(commandAutoCompleteInteractionEvent);
        }
        return messageExecutor;
    }

    private void replyWithDeletableCountdowns(
            CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
        var countdowns = countdownRepository.findCountdownByCreatedBy(commandAutoCompleteInteractionEvent.getMember()
                        .getId())
                .stream()
                .map(Countdown::getName)
                .limit(OptionData.MAX_CHOICES)
                .toList();
        if (!countdowns.isEmpty()) {
            commandAutoCompleteInteractionEvent.replyChoiceStrings(countdowns).queue();
        }
    }

    @Override
    public Class<CountdownCommandName.Subcommand> enumSubcommandClass() {
        return CountdownCommandName.Subcommand.class;
    }
}
