package dev.nincodedo.ninbot.components.countdown;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CountdownCommandAutoComplete extends ListenerAdapter {

    CountdownRepository countdownRepository;

    CountdownCommandAutoComplete(CountdownRepository countdownRepository) {
        this.countdownRepository = countdownRepository;
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        if (CountdownCommandName.COUNTDOWN.get().equals(event.getName()) && CountdownCommandName.Subcommand.DELETE.get()
                .equals(event.getSubcommandName())) {
            var countdowns = countdownRepository.findCountdownByCreatorId(event.getMember().getId())
                    .stream()
                    .map(Countdown::getName)
                    .toList();
            event.replyChoiceStrings(countdowns).queue();
        }
    }
}
