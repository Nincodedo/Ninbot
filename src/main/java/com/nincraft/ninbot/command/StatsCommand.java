package com.nincraft.ninbot.command;

import com.nincraft.ninbot.util.MessageSenderHelper;
import com.nincraft.ninbot.util.Reference;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsCommand extends AbstractCommand {

    private List<String> roleBlacklist;

    public StatsCommand() {
        roleBlacklist = Reference.getRoleBlacklist();
        length = 2;
        name = "stats";
        description = "Shows Ninbot stats";
    }

    @Override
    public void executeCommand(MessageReceivedEvent event) {
        val server = event.getGuild();
        printRoleStats(event.getChannel(), server);
    }

    private void printRoleStats(MessageChannel channel, Guild server) {
        Map<Role, Integer> roleMap = new HashMap<>();
        for (val member : server.getMembers()) {
            for (val role : member.getRoles()) {
                if (!roleBlacklist.contains(role.getName())) {
                    roleMap.merge(role, 1, (a, b) -> a + b);
                }
            }
        }

        List<Stat> statList = roleMap.keySet().stream().map(role -> new Stat(role.getName(), roleMap.get(role))).collect(Collectors.toList());

        StringBuilder stringBuilder = new StringBuilder();
        Collections.sort(statList);
        statList.forEach(stat -> {
            stringBuilder.append(stat.name);
            stringBuilder.append(": ");
            stringBuilder.append(stat.amount);
            stringBuilder.append("\n");
        });
        MessageSenderHelper.sendMessage(channel, stringBuilder.toString());
    }

    private class Stat implements Comparable {
        private String name;
        private int amount;

        Stat(String name, int amount) {
            this.name = name;
            this.amount = amount;
        }


        @Override
        public int compareTo(Object o) {
            Stat stat = (Stat) o;
            if (stat.amount == this.amount) {
                return stat.name.compareTo(this.name);
            } else {
                return Integer.compare(stat.amount, this.amount);
            }
        }
    }
}
