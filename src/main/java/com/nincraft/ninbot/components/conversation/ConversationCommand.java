package com.nincraft.ninbot.components.conversation;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class ConversationCommand extends AbstractCommand {

    private ConfigService configService;

    public ConversationCommand(ConfigService configService) {
        name = "conversation";
        length = 2;
        description = "Toggles Ninbot conversation mode in a channel";
        permissionLevel = RolePermission.MODS;
        this.configService = configService;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        val serverId = event.getGuild().getId();
        val conversationChannelList = configService.getValuesByName(serverId, ConfigConstants.CONVERSATION_CHANNELS);
        val channelId = event.getChannel().getId();
        Config config = new Config(serverId, ConfigConstants.CONVERSATION_CHANNELS, channelId);
        if (conversationChannelList.contains(channelId)) {
            configService.addConfig(config);
        } else {
            configService.removeConfig(config);
        }
        messageUtils.reactSuccessfulResponse(event.getMessage());
    }
}
