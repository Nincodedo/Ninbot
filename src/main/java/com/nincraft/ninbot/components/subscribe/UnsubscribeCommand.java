package com.nincraft.ninbot.components.subscribe;

import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import org.springframework.stereotype.Component;

@Component
public class UnsubscribeCommand extends SubscribeCommand {

    public UnsubscribeCommand(ConfigService configService) {
        super(configService);
        name = "unsubscribe";
        description = "Unsubscribes you from a game for game gathering events";
    }

    @Override
    void addOrRemoveSubscription(MessageReceivedEvent event, GuildController controller, Role role) {
        controller.removeRolesFromMember(event.getMember(), role).queue();
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }
}
