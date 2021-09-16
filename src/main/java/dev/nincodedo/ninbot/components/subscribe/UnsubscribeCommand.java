package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.interactions.InteractionHook;
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
    void addOrRemoveSubscription(InteractionHook interactionHook, Member member, Guild guild,
            Role role) throws PermissionException {
        guild.removeRoleFromMember(member, role).queue(successAction(interactionHook), failureAction(interactionHook));
    }
}
