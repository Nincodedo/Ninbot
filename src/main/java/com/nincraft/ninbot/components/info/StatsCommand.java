package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatsCommand extends AbstractCommand {

    private ConfigService configService;

    public StatsCommand(ConfigService configService) {
        length = 2;
        name = "stats";
        this.configService = configService;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        return commandResult.addChannelAction(displayRoleStats(event.getGuild()));
    }

    private Message displayRoleStats(Guild server) {
        List<String> roleBlackList = configService.getValuesByName(server.getId(), ConfigConstants.ROLE_BLACKLIST);

        Map<Role, Integer> roleMap = server.getMembers().stream().flatMap(member -> member.getRoles().stream())
                .filter(role -> !roleBlackList.contains(role.getName()))
                .collect(Collectors.toMap(role -> role, role -> 1, Integer::sum));

        List<Stat> statList = roleMap.keySet()
                .stream()
                .map(role -> new Stat(role.getName(), roleMap.get(role)))
                .collect(Collectors.toList());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        int limit = 5;
        embedBuilder.setTitle(String.format(resourceBundle.getString("command.stats.list.title"), limit));
        Collections.sort(statList);
        statList.stream().limit(limit).forEach(stat -> embedBuilder.appendDescription(
                stat.name + ": " + stat.amount + "\n"));
        return new MessageBuilder(embedBuilder).build();
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
