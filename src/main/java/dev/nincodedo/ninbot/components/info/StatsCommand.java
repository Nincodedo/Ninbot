package dev.nincodedo.ninbot.components.info;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StatsCommand extends AbstractCommand {

    public StatsCommand() {
        length = 2;
        name = "stats";
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        return messageAction.addChannelAction(displayRoleStats(event.getGuild()));
    }

    private Message displayRoleStats(Guild server) {
        List<String> roleDenyList = configService.getValuesByName(server.getId(), ConfigConstants.ROLE_DENY_LIST);

        Map<Role, Integer> roleMap = server.getMembers().stream().flatMap(member -> member.getRoles().stream())
                .filter(role -> !roleDenyList.contains(role.getName()))
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

    private record Stat(String name, int amount) implements Comparable<Stat> {
        @Override
        public int compareTo(Stat other) {
            return Comparator.comparing(Stat::amount).thenComparing(Stat::name).compare(this, other);
        }
    }
}