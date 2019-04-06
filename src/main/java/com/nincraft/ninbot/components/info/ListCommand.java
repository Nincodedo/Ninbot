package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ListCommand extends AbstractCommand {

    private ConfigService configService;

    public ListCommand(ConfigService configService) {
        length = 2;
        name = "list";
        this.configService = configService;
    }

    @Override
    public CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val content = event.getMessage().getContentStripped().split("\\s+");
        if (content.length > 2) {
            listUsersInSubscription(content[2], event.getGuild()).ifPresent(commandResult::addChannelAction);
        } else {
            commandResult.addChannelAction(listSubscriptions(event.getGuild().getRoles(), event.getGuild().getId()));
        }
        return commandResult;
    }

    private Optional<Message> listUsersInSubscription(String roleName, Guild guild) {
        val role = guild.getRolesByName(roleName, true);
        if (!role.isEmpty()) {
            val users = guild.getMembersWithRoles(role);
            List<String> userNames = users.stream().map(Member::getEffectiveName).sorted().collect(Collectors.toList());
            MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
            messageBuilder.setTitle(String.format(resourceBundle.getString("command.list.usersinsub"), roleName));
            messageBuilder.appendDescription(userNames.toString());
            return Optional.of(messageBuilder.build());
        }
        return Optional.empty();
    }

    private Message listSubscriptions(List<Role> roleList, String guildId) {
        List<String> roleNameList = roleList.stream().map(Role::getName).collect(Collectors.toList());
        List<String> roleBlackList = configService.getValuesByName(guildId, ConfigConstants.ROLE_BLACKLIST);
        roleNameList.removeAll(roleBlackList);
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle(resourceBundle.getString("command.list.availablesubs"));
        roleNameList.stream().map(roleName -> roleName + "\n").forEach(messageBuilder::appendDescription);
        return messageBuilder.build();
    }
}
