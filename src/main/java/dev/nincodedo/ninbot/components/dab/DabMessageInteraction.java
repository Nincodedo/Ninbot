package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.ninbot.common.command.message.MessageContextCommand;
import dev.nincodedo.ninbot.common.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.ninbot.common.message.MessageExecutor;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class DabMessageInteraction implements MessageContextCommand {

    private Dabber dabber;

    DabMessageInteraction(Dabber dabber) {
        this.dabber = dabber;
    }

    @Override
    public MessageExecutor<MessageContextInteractionEventMessageExecutor> execute(
            @NotNull MessageContextInteractionEvent event) {
        MessageExecutor<MessageContextInteractionEventMessageExecutor> messageExecutor =
                new MessageContextInteractionEventMessageExecutor(event);
        messageExecutor.setOverrideMessage(event.getInteraction().getTarget());
        dabber.dabOnMessage(messageExecutor, event.getJDA().getShardManager(), event.getUser());
        messageExecutor.addMessageResponse(new MessageBuilder().append(event.getJDA()
                        .getGuildById("497444318768922633")
                        .getEmotesByName("ninbotdab", true)
                        .get(0))
                .append(" ")
                .append(event.getTarget().getAuthor())
                .build());
        return messageExecutor;
    }

    @Override
    public String getName() {
        return DabCommandName.DAB.get();
    }
}
