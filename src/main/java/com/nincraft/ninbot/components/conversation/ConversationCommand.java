package com.nincraft.ninbot.components.conversation;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class ConversationCommand extends AbstractCommand {

    private ConfigService configService;

    public ConversationCommand(ConfigService configService) {
        name = "conversation";
        length = 2;
        permissionLevel = RolePermission.MODS;
        this.configService = configService;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        val serverId = event.getGuild().getId();
        val conversationChannelList = configService.getValuesByName(serverId, ConfigConstants.CONVERSATION_CHANNELS);
        val channelId = event.getChannel().getId();
        if (!conversationChannelList.contains(channelId)) {
            configService.addConfig(serverId, ConfigConstants.CONVERSATION_CHANNELS, channelId);
        } else {
            configService.removeConfig(serverId, ConfigConstants.CONVERSATION_CHANNELS, channelId);
        }
        return commandResult.addSuccessfulReaction();
    }
}
