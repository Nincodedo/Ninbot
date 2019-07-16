package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class TwitchCommand extends AbstractCommand {

    private ConfigService configService;

    public TwitchCommand(ConfigService configService) {
        name = "twitch";
        length = 2;
        this.configService = configService;
        helpText = "Access Twitch related commands\n\"twitch announce\" toggles your Twitch going live announcement";
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "announce":
                commandResult.addReaction(announceToggle(event));
                break;
            default:
                commandResult.addUnknownReaction();
                break;
        }
        return commandResult;
    }

    private String announceToggle(MessageReceivedEvent event) {
        val userId = event.getAuthor().getId();
        val serverId = event.getGuild().getId();
        val configName = ConfigConstants.STREAMING_ANNOUNCE_USERS;
        val streamingAnnounceUsers = configService.getValuesByName(serverId, configName);
        String responseEmoji;
        if (streamingAnnounceUsers.contains(userId)) {
            configService.removeConfig(serverId, configName, userId);
            responseEmoji = Emojis.OFF;
        } else {
            configService.addConfig(serverId, configName, userId);
            responseEmoji = Emojis.ON;
        }
        return responseEmoji;
    }
}
