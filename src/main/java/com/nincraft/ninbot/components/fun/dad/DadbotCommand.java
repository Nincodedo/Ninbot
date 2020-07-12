package com.nincraft.ninbot.components.fun.dad;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class DadbotCommand extends AbstractCommand {

    public DadbotCommand() {
        name = "dad";
        length = 3;
        checkExactLength = false;
        permissionLevel = RolePermission.MODS;
    }

    @Override
    public MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        toggleDenyListChannel(event.getGuild().getId(), event.getChannel().getId());
        messageAction.addSuccessfulReaction();
        return messageAction;
    }

    private void toggleDenyListChannel(String serverId, String channelId) {
        val channelDenyList = configService.getValuesByName(serverId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL);
        if (!channelDenyList.contains(channelId)) {
            configService.addConfig(serverId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL, channelId);
        } else {
            configService.removeConfig(serverId, ConfigConstants.DADBOT_DENY_LIST_CHANNEL, channelId);
        }
    }
}
