package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.*;

public class StatsCommand extends AbstractCommand {

    private List<String> roleBlacklist;

    public StatsCommand() {
        roleBlacklist = Reference.getRoleBlacklist();
        commandLength = 2;
        commandName = "stats";
        commandDescription = "Shows Ninbot stats";
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        val server = event.getGuild();
        printRoleStats(event, server);
    }

    private void printRoleStats(MessageReceivedEvent event, Guild server) {
        Map<Role, Integer> roleMap = new HashMap<>();
        for (val member : server.getMembers()) {
            for (val role : member.getRoles()) {
                if (!roleBlacklist.contains(role.getName())) {
                    roleMap.merge(role, 1, (a, b) -> a + b);
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<Role> roleList = new ArrayList(roleMap.keySet());
        Collections.sort(roleList);
        Collections.reverse(roleList);
        roleList.forEach(role -> {
            stringBuilder.append(role.getName());
            stringBuilder.append(": ");
            stringBuilder.append(roleMap.get(role));
            stringBuilder.append("\n");
        });
        MessageSenderHelper.sendMessage(event.getChannel(), stringBuilder.toString());
    }
}
