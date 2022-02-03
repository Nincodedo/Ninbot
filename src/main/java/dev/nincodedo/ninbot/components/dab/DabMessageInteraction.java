package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class DabMessageInteraction extends ListenerAdapter {

    private Dabber dabber;

    DabMessageInteraction(Dabber dabber) {
        this.dabber = dabber;
    }

    @Override
    public void onMessageContextInteraction(@NotNull MessageContextInteractionEvent event) {
        if (DabCommandName.DAB.get().equals(event.getName())) {
            MessageExecutor<MessageContextInteractionEventMessageExecutor> messageExecutor =
                    new MessageContextInteractionEventMessageExecutor(event);
            dabber.dabOnMessage(event.getInteraction().getTarget().getId());
        }
    }
}
