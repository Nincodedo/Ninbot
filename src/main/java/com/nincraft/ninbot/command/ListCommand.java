package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends AbstractCommand {

    public ListCommand() {
        length = 2;
        name = "list";
        description = "Displays available games for subscribing";
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val channel = event.getChannel();
        val content = event.getMessage().getContent().split(" ");
        if (content.length > 2) {
            listUsersInSubscription(content[2], event.getGuild(), event.getChannel());
        } else {
            listSubscriptions(event.getGuild(), channel);
        }
    }

    private void listUsersInSubscription(String roleName, Guild guild, MessageChannel channel) {
        val role = guild.getRolesByName(roleName, true);
        if (!role.isEmpty()) {
            val users = guild.getMembersWithRoles(role);
            List<String> userNames = users.stream().map(Member::getEffectiveName).collect(Collectors.toList());
            Collections.sort(userNames);
            MessageSenderHelper.sendMessage(channel, "Users in %s subscription", roleName);
            MessageSenderHelper.sendMessage(channel, userNames.toString());
        } else {
            MessageSenderHelper.sendMessage(channel, "No subscription %s found", roleName);
        }
    }

    private void listSubscriptions(Guild guild, MessageChannel channel) {
        val roleList = guild.getRoles();
        List<String> roleNameList = roleList.stream().map(Role::getName).collect(Collectors.toList());
        roleNameList.removeAll(Reference.getRoleBlacklist());
        MessageSenderHelper.sendMessage(channel, "Available subscriptions");
        MessageSenderHelper.sendMessage(channel, roleNameList.toString());
    }
}
