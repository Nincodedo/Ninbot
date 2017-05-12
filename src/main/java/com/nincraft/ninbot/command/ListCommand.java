package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.val;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.List;
import java.util.stream.Collectors;

public class ListCommand extends AbstractCommand {

    public ListCommand() {
        commandLength = 2;
        commandName = "list";
        commandDescription = "Displays available games for subscribing";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        val channel = event.getChannel();
        val roleList = event.getGuild().getRoles();
        List<String> roleNameList = roleList.stream().map(Role::getName).collect(Collectors.toList());
        roleNameList.removeAll(Reference.getRoleBlacklist());
        MessageSenderHelper.sendMessage(channel, "Available subscriptions");
        MessageSenderHelper.sendMessage(channel, roleNameList.toString());
    }
}
