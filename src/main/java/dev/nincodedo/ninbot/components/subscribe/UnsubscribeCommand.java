package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

@Component
public class UnsubscribeCommand extends SubscribeCommand {

    public UnsubscribeCommand(ConfigService configService) {
        super(configService);
    }

    @Override
    public String getName() {
        return "unsubscribe";
    }

    @Override
    void addOrRemoveSubscription(SlashCommandEvent slashCommandEvent, Guild guild, Role role) {
        guild.removeRoleFromMember(slashCommandEvent.getMember(), role).queue();
    }
}
