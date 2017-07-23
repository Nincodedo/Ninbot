package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

@Log4j2
public class SubscribeCommand extends AbstractCommand {

    private List<String> roleBlacklist;

    public SubscribeCommand() {
        roleBlacklist = Reference.getRoleBlacklist();
        commandLength = 3;
        commandName = "subscribe";
        commandDescription = "Subscribes you to a game for game gathering events";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        log.trace("execute SubscribeCommand: " + event);
        val content = event.getMessage().getContent().toLowerCase();
        val channel = event.getChannel();
        if (isCommandLengthCorrect(content)) {
            val server = event.getGuild();
            val subscribeTo = content.split(" ")[2];
            val role = getRole(server, subscribeTo);
            if (isValidSubscribeRole(role)) {
                addOrRemoveSubscription(event, channel, server.getController(), subscribeTo, role);
            } else {
                MessageSenderHelper.sendMessage(channel, "Could not find the role \"%s\", contact an admin", subscribeTo);
            }
        } else {
            wrongCommandLengthMessage(channel);
        }
    }

    void addOrRemoveSubscription(MessageReceivedEvent event, MessageChannel channel, GuildController controller, String subscribeTo, Role role) {
        MessageSenderHelper.sendMessage(channel, "Subscribing %s to %s", event.getAuthor().getName(), subscribeTo);
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
