package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.command.CommandResult;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.config.Config;
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
    }

    @Override
    protected CommandResult executeCommand(MessageReceivedEvent event) {
        CommandResult commandResult = new CommandResult(event);
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "announce":
                announceToggle(commandResult);
                break;
            default:
                commandResult = displayHelp(event);
                break;
        }
        return commandResult;
    }

    private void announceToggle(CommandResult commandResult) {
        val event = commandResult.getEvent();
        val userId = event.getAuthor().getId();
        val serverId = event.getGuild().getId();
        val configName = ConfigConstants.STREAMING_ANNOUNCE_USERS;
        val streamingAnnounceUsers = configService.getConfigByName(serverId, configName);
        boolean foundUser = false;
        for (Config config : streamingAnnounceUsers) {
            if (config.getValue().equals(userId)) {
                configService.removeConfig(config);
                commandResult.addReaction(Emojis.OFF);
                foundUser = true;
            }
        }
        if (!foundUser) {
            configService.addConfig(serverId, configName, userId);
            commandResult.addReaction(Emojis.ON);
        }
    }
}
