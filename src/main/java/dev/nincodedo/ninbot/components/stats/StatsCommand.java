package dev.nincodedo.ninbot.components.stats;

import dev.nincodedo.ninbot.common.command.AbstractCommand;
import dev.nincodedo.ninbot.common.message.MessageReceivedEventMessageExecutor;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import org.apache.commons.text.WordUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatsCommand extends AbstractCommand {

    private StatManager statManager;

    public StatsCommand(StatManager statManager) {
        length = 2;
        name = "stats";
        checkExactLength = false;
        aliases = Collections.singletonList("stat");
        this.statManager = statManager;
    }

    //TODO implement SlashCommand
    @Override
    protected MessageReceivedEventMessageExecutor executeCommand(PrivateMessageReceivedEvent event) {
        MessageReceivedEventMessageExecutor messageAction = new MessageReceivedEventMessageExecutor(event);

        return messageAction;
    }

    private Message displayServerStats(MessageReceivedEvent event) {
        var statMap = statManager.getStatMapByServerId(event.getGuild().getId());
        EmbedBuilder embedBuilder = new EmbedBuilder();
        for (var key : statMap.keySet()) {
            var statList = statMap.get(key)
                    .stream()
                    .sorted(Comparator.comparing(Stat::getCategory))
                    .limit(5)
                    .collect(Collectors.toList());
            embedBuilder.addField(WordUtils.capitalizeFully(key), "", false);
            for (var stat : statList) {
                embedBuilder.addField(WordUtils.capitalizeFully(stat.getName()), String.valueOf(stat.getCount()),
                        false);
            }
        }

        return new MessageBuilder(embedBuilder).build();
    }

    private Message displayRoleStats(Guild server) {
        List<String> roleDenyList = configService.getValuesByName(server.getId(), ConfigConstants.ROLE_DENY_LIST);

        Map<Role, Integer> roleMap = server.getMembers().stream().flatMap(member -> member.getRoles().stream())
                .filter(role -> !roleDenyList.contains(role.getName()))
                .collect(Collectors.toMap(role -> role, role -> 1, Integer::sum));

        List<RoleStat> roleStatList = roleMap.keySet()
                .stream()
                .map(role -> new RoleStat(role.getName(), roleMap.get(role)))
                .collect(Collectors.toList());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        int limit = 5;
        embedBuilder.setTitle(String.format(resourceBundle.getString("command.stats.list.title"), limit));
        Collections.sort(roleStatList);
        roleStatList.stream().limit(limit).forEach(roleStat -> embedBuilder.appendDescription(
                roleStat.name + ": " + roleStat.amount + "\n"));
        return new MessageBuilder(embedBuilder).build();
    }

    private record RoleStat(String name, int amount) implements Comparable<RoleStat> {
        @Override
        public int compareTo(RoleStat other) {
            return Comparator.comparing(RoleStat::amount).thenComparing(RoleStat::name).compare(this, other);
        }
    }
}
