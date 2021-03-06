package dev.nincodedo.ninbot.components.subscribe;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ListCommand extends AbstractCommand {

    public ListCommand() {
        length = 2;
        name = "list";
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val content = event.getMessage().getContentStripped().split("\\s+");
        if (content.length > 2) {
            listUsersInSubscription(content[2], event.getGuild()).ifPresent(messageAction::addChannelAction);
        } else {
            messageAction.addChannelAction(listSubscriptions(event.getGuild().getRoles(), event.getGuild().getId()));
        }
        return messageAction;
    }

    private Optional<Message> listUsersInSubscription(String roleName, Guild guild) {
        val role = guild.getRolesByName(roleName, true);
        if (!role.isEmpty()) {
            val users = guild.getMembersWithRoles(role);
            List<String> userNames = users.stream().map(Member::getEffectiveName).sorted().collect(Collectors.toList());
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(String.format(resourceBundle.getString("command.list.usersinsub"), roleName));
            embedBuilder.appendDescription(userNames.toString());
            return Optional.of(new MessageBuilder(embedBuilder).build());
        }
        return Optional.empty();
    }

    private Message listSubscriptions(List<Role> roleList, String guildId) {
        List<String> roleNameList = roleList.stream()
                .filter(role -> !role.isManaged() && role.getPermissions().size() <= roleList.get(roleList.size() - 1)
                        .getPermissions()
                        .size() && !role.getName().equals("infected") && !role.getName().equals("@everyone"))
                .map(Role::getName)
                .collect(Collectors.toList());
        List<String> roleDenyList = configService.getValuesByName(guildId, ConfigConstants.ROLE_DENY_LIST);
        roleNameList.removeAll(roleDenyList);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(resourceBundle.getString("command.list.availablesubs"));
        roleNameList.stream().map(roleName -> roleName + "\n").forEach(embedBuilder::appendDescription);
        return new MessageBuilder(embedBuilder).build();
    }
}
