package com.nincraft.ninbot.components.stream;

import com.nincraft.ninbot.components.command.AbstractCommand;
import com.nincraft.ninbot.components.common.Emojis;
import com.nincraft.ninbot.components.common.MessageAction;
import com.nincraft.ninbot.components.config.Config;
import com.nincraft.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class StreamCommand extends AbstractCommand {

    public StreamCommand() {
        name = "stream";
        length = 2;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        switch (getSubcommand(event.getMessage().getContentStripped())) {
            case "announce" -> announceToggle(messageAction);
            default -> messageAction = displayHelp(event);
        }
        return messageAction;
    }

    private void announceToggle(MessageAction messageAction) {
        val event = messageAction.getEvent();
        val userId = event.getAuthor().getId();
        val serverId = event.getGuild().getId();
        val configName = ConfigConstants.STREAMING_ANNOUNCE_USERS;
        val streamingAnnounceUsers = configService.getConfigByName(serverId, configName);
        boolean foundUser = false;
        for (Config config : streamingAnnounceUsers) {
            if (config.getValue().equals(userId)) {
                configService.removeConfig(config);
                messageAction.addReaction(Emojis.OFF);
                foundUser = true;
                break;
            }
        }
        if (!foundUser) {
            configService.addConfig(serverId, configName, userId);
            messageAction.addReaction(Emojis.ON);
        }
    }
}
