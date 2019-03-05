package com.nincraft.ninbot.components.info;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.entities.*;
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
        description = "Displays available games for subscribing";
        this.configService = configService;
    }

    @Override
    public Optional<CommandResult> executeCommand(MessageReceivedEvent event) {
        val channel = event.getChannel();
        val content = event.getMessage().getContentStripped().split("\\s+");
        if (content.length > 2) {
            listUsersInSubscription(content[2], event.getGuild(), event.getChannel(), event.getMessage());
        } else {
            listSubscriptions(event.getGuild(), channel);
        }
    }

    private void listUsersInSubscription(String roleName, Guild guild, MessageChannel channel,
            Message message) {
        val role = guild.getRolesByName(roleName, true);
        if (!role.isEmpty()) {
            val users = guild.getMembersWithRoles(role);
            List<String> userNames = users.stream().map(Member::getEffectiveName).sorted().collect(Collectors.toList());
            MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
            messageBuilder.setTitle("Users in " + roleName + " subscription");
            messageBuilder.appendDescription(userNames.toString());
            messageUtils.sendMessage(channel, messageBuilder.build());
        } else {
            messageUtils.reactUnsuccessfulResponse(message);
        }
    }

    private void listSubscriptions(Guild guild, MessageChannel channel) {
        val roleList = guild.getRoles();
        List<String> roleNameList = roleList.stream().map(Role::getName).collect(Collectors.toList());
        List<String> roleBlackList = configService.getValuesByName(guild.getId(), ConfigConstants.ROLE_BLACKLIST);
        roleNameList.removeAll(roleBlackList);
        MessageBuilderHelper messageBuilder = new MessageBuilderHelper();
        messageBuilder.setTitle("Available subscriptions");
        roleNameList.stream().map(roleName -> roleName + "\n").forEach(messageBuilder::appendDescription);
        messageUtils.sendMessage(channel, messageBuilder.build());
    }
}
