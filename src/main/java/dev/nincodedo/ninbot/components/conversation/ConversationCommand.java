package dev.nincodedo.ninbot.components.conversation;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.RolePermission;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class ConversationCommand extends AbstractCommand {
    public ConversationCommand() {
        name = "conversation";
        length = 2;
        permissionLevel = RolePermission.MODS;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        String serverId = event.getGuild().getId();
        final java.util.List<java.lang.String> conversationChannelList = configService.getValuesByName(serverId,
                ConfigConstants.CONVERSATION_CHANNELS);
        String channelId = event.getChannel().getId();
        if (!conversationChannelList.contains(channelId)) {
            configService.addConfig(serverId, ConfigConstants.CONVERSATION_CHANNELS, channelId);
        } else {
            configService.removeConfig(serverId, ConfigConstants.CONVERSATION_CHANNELS, channelId);
        }
        return messageAction.addSuccessfulReaction();
    }
}
