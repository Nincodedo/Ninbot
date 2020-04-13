package com.nincraft.ninbot.components.channel;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class TopicChangeCommand extends AbstractCommand {

    public TopicChangeCommand() {
        name = "topic-change";
        length = 2;
        permissionLevel = RolePermission.MODS;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);

        val channelConfig = configService.getConfigByServerIdAndName(event.getGuild()
                .getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);

        if (channelConfig.isPresent()) {
            configService.removeConfig(channelConfig.get());
            messageAction.addReaction(Emojis.OFF);
        } else {
            Config config = new Config(event.getGuild()
                    .getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL, event.getChannel().getId());
            configService.addConfig(config);
            messageAction.addReaction(Emojis.ON);
        }
        return messageAction;
    }
}
