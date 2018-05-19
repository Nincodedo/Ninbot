package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.util.MessageUtils;
import lombok.val;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class TwitchCommand extends AbstractCommand {

    private ConfigService configService;

    public TwitchCommand(ConfigService configService) {
        name = "twitch";
        description = "Access Twitch related commands\n - \"twitch announce\" toggles your Twitch going live announcement";
        length = 2;
        this.configService = configService;
    }

    @Override
    protected void executeCommand(MessageReceivedEvent event) {
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "announce":
                announceToggle(event);
                break;
            default:
                break;
        }
    }

    private void announceToggle(MessageReceivedEvent event) {
        val userId = event.getAuthor().getId();
        val serverId = event.getGuild().getId();
        val configName = "streamingAnnounceUsers";
        val streamingAnnounceUsers = configService.getValuesByName(serverId, configName);
        if (streamingAnnounceUsers.contains(userId)) {
            configService.removeConfig(serverId, configName, userId);
        } else {
            configService.addConfig(serverId, configName, userId);
        }
        MessageUtils.reactSuccessfulResponse(event.getMessage());
    }
}
