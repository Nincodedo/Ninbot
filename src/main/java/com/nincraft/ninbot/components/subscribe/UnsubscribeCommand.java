package com.nincraft.ninbot.components.subscribe;

import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class UnsubscribeCommand extends SubscribeCommand {

    public UnsubscribeCommand(ConfigService configService) {
        super(configService);
        name = "unsubscribe";
    }

    @Override
    void addOrRemoveSubscription(MessageReceivedEvent event, Guild guild, Role role) {
        guild.removeRoleFromMember(event.getMember(), role).queue();
    }
}
