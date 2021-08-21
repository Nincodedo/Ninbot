package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.fun.pathogen.PathogenConfig;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscribeCommand extends AbstractCommand {

    public SubscribeCommand() {
        length = 3;
        name = "subscribe";
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        var content = event.getMessage().getContentStripped().toLowerCase();
        if (isCommandLengthCorrect(content)) {
            var server = event.getGuild();
            var subscribeTo = content.split("\\s+")[2];
            var role = getRole(server, subscribeTo);
            if (isValidSubscribeRole(role, event.getGuild().getId())) {
                addOrRemoveSubscription(event, server, role);
                messageAction.addSuccessfulReaction();
            } else {
                messageAction.addChannelAction(new MessageBuilder()
                        .appendFormat(resourceBundle.getString("command.subscribe.norolefound"), subscribeTo)
                        .build());
            }
        } else {
            messageAction.addUnknownReaction();
        }
        return messageAction;
    }

    void addOrRemoveSubscription(MessageReceivedEvent event, Guild guild, Role role) {
        guild.addRoleToMember(event.getMember(), role).queue();
    }

    private boolean isValidSubscribeRole(Role role, String serverId) {
        List<String> roleDenyList = configService.getValuesByName(serverId, ConfigConstants.ROLE_DENY_LIST);
        roleDenyList.add(PathogenConfig.getINFECTED_ROLE_NAME());
        return role != null && !roleDenyList.contains(role.getName());
    }

    private Role getRole(Guild server, String subscribeTo) {
        var roleList = server.getRolesByName(subscribeTo, true);
        return roleList.isEmpty() ? null : roleList.get(0);
    }
}
