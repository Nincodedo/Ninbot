package dev.nincodedo.ninbot.components.countdown;

import dev.nincodedo.nincord.command.AutoCompleteCommand;
import dev.nincodedo.nincord.command.Subcommand;
import dev.nincodedo.nincord.message.AutoCompleteCommandMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
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
    public MessageExecutor execute(@NotNull CommandAutoCompleteInteractionEvent commandAutoCompleteInteractionEvent) {
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
        var countdowns =
                countdownRepository.findCountdownByAudit_CreatedBy(commandAutoCompleteInteractionEvent.getMember()
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
