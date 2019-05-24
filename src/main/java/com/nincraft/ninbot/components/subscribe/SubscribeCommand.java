package com.nincraft.ninbot.components.subscribe;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class SubscribeCommand extends AbstractCommand {

    private ConfigService configService;

    public SubscribeCommand(ConfigService configService) {
        length = 3;
        name = "subscribe";
        this.configService = configService;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val content = event.getMessage().getContentStripped().toLowerCase();
        if (isCommandLengthCorrect(content)) {
            val server = event.getGuild();
            val subscribeTo = content.split("\\s+")[2];
            val role = getRole(server, subscribeTo);
            if (isValidSubscribeRole(role, event.getGuild().getId())) {
                addOrRemoveSubscription(event, server, role);
                commandResult.addSuccessfulReaction();
            } else {
                commandResult.addChannelAction(new MessageBuilder()
                        .appendFormat(resourceBundle.getString("command.subscribe.norolefound"), subscribeTo)
                        .build());
            }
        } else {
            commandResult.addUnknownReaction();
        }
        return commandResult;
    }

    void addOrRemoveSubscription(MessageReceivedEvent event, Guild guild, Role role) {
        guild.addRoleToMember(event.getMember(), role).queue();
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
