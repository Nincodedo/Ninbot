package com.nincraft.ninbot.components.subscribe;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.util.MessageUtils;
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

    private List<String> roleBlacklist;

    public SubscribeCommand(List<String> roleBlackList) {
        this.roleBlacklist = roleBlackList;
        length = 3;
        name = "subscribe";
        description = "Subscribes you to a game for game gathering events";
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val content = event.getMessage().getContentStripped().toLowerCase();
        val channel = event.getChannel();
        if (isCommandLengthCorrect(content)) {
            val server = event.getGuild();
            val subscribeTo = content.split(" ")[2];
            val role = getRole(server, subscribeTo);
            if (isValidSubscribeRole(role)) {
                addOrRemoveSubscription(event, server.getController(), role);
            } else {
                MessageUtils.sendMessage(channel, "Could not find the role \"%s\", contact an admin", subscribeTo);
            }
        } else {
            wrongCommandLengthMessage(channel);
        }
    }

    void addOrRemoveSubscription(MessageReceivedEvent event, GuildController controller, Role role) {
        MessageUtils.reactSuccessfulResponse(event.getMessage());
        controller.addRolesToMember(event.getMember(), role).queue();
    }

    private boolean isValidSubscribeRole(Role role) {
        return role != null && !roleBlacklist.contains(role.getName());
    }

    private Role getRole(Guild server, String subscribeTo) {
        val roleList = server.getRolesByName(subscribeTo, true);
        return roleList.isEmpty() ? null : roleList.get(0);
    }
}
