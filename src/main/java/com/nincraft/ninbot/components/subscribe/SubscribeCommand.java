package com.nincraft.ninbot.components.subscribe;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class SubscribeCommand extends AbstractCommand {

    private ConfigService configService;

    public SubscribeCommand(ConfigService configService) {
        length = 3;
        name = "subscribe";
        description = "Subscribes you to a game for game gathering events";
        this.configService = configService;
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val content = event.getMessage().getContentStripped().toLowerCase();
        val channel = event.getChannel();
        if (isCommandLengthCorrect(content)) {
            val server = event.getGuild();
            val subscribeTo = content.split("\\s+")[2];
            val role = getRole(server, subscribeTo);
            if (isValidSubscribeRole(role, event.getGuild().getId())) {
                addOrRemoveSubscription(event, server.getController(), role);
            } else {
                messageUtils.sendMessage(channel, "Could not find the role \"%s\", contact an admin", subscribeTo);
            }
        } else {
            messageUtils.reactUnknownResponse(event.getMessage());
        }
    }

    void addOrRemoveSubscription(MessageReceivedEvent event, GuildController controller, Role role) {
        messageUtils.reactSuccessfulResponse(event.getMessage());
        controller.addRolesToMember(event.getMember(), role).queue();
    }

    private boolean isValidSubscribeRole(Role role, String serverId) {
        List<String> roleBlacklist = configService.getValuesByName(serverId, ConfigConstants.ROLE_BLACKLIST);
        return role != null && !roleBlacklist.contains(role.getName());
    }

    private Role getRole(Guild server, String subscribeTo) {
        val roleList = server.getRolesByName(subscribeTo, true);
        return roleList.isEmpty() ? null : roleList.get(0);
    }
}
