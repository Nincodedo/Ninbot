package com.nincraft.ninbot.components.channel;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.RolePermission;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class TopicChangeCommand extends AbstractCommand {

    private ConfigService configService;

    public TopicChangeCommand(ConfigService configService) {
        name = "topic-change";
        length = 2;
        permissionLevel = RolePermission.MODS;
        this.configService = configService;
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);

        val channelConfig = configService.getConfigByServerIdAndName(event.getGuild()
                .getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL);

        if (channelConfig.isPresent()) {
            configService.removeConfig(channelConfig.get());
            commandResult.addReaction(Emojis.OFF);
        } else {
            Config config = new Config(event.getGuild()
                    .getId(), ConfigConstants.TOPIC_CHANGE_CHANNEL, event.getChannel().getId());
            configService.addConfig(config);
            commandResult.addReaction(Emojis.ON);
        }
        return commandResult;
    }
}
