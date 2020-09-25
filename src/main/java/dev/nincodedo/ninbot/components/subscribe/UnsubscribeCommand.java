package dev.nincodedo.ninbot.components.subscribe;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class UnsubscribeCommand extends SubscribeCommand {

    public UnsubscribeCommand() {
        super();
        name = "unsubscribe";
    }

    @Override
    void addOrRemoveSubscription(MessageReceivedEvent event, Guild guild, Role role) {
        guild.removeRoleFromMember(event.getMember(), role).queue();
    }
}
