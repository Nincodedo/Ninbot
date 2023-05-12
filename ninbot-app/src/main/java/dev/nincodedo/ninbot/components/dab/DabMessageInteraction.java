package dev.nincodedo.ninbot.components.dab;

import dev.nincodedo.nincord.command.message.MessageContextCommand;
import dev.nincodedo.nincord.message.MessageContextInteractionEventMessageExecutor;
import dev.nincodedo.nincord.message.MessageExecutor;
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
    public MessageExecutor execute(@NotNull MessageContextInteractionEvent event,
            @NotNull MessageContextInteractionEventMessageExecutor messageExecutor) {
        messageExecutor.setOverrideMessage(event.getInteraction().getTarget());
        dabber.dabOnMessage(messageExecutor, event.getJDA().getShardManager(), event.getUser());
        var user = event.getTarget().getAuthor();
        var guild = event.getGuild();
        if (guild != null) {
            var member = guild.getMember(user);
            if (member != null) {
                messageExecutor.addMessageResponse(dabber.buildDabMessage(member));
            }
        }
        return messageExecutor;
    }

    @Override
    public String getName() {
        return DabCommandName.DAB.get();
    }
}
