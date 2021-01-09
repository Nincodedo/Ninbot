package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.Config;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import lombok.val;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;

@Component
public class StreamCommand extends AbstractCommand {

    private StreamingMemberRepository streamingMemberRepository;

    public StreamCommand(StreamingMemberRepository streamingMemberRepository) {
        name = "stream";
        length = 2;
        checkExactLength = false;
        this.streamingMemberRepository = streamingMemberRepository;
    }

    @Override
    protected MessageAction executeCommand(MessageReceivedEvent event) {
        MessageAction messageAction = new MessageAction(event);
        val message = event.getMessage().getContentStripped();
        if ("announce".equals(getSubcommand(message))) {
            if (getCommandLength(message) == 3) {
                announceToggle(messageAction);
            } else if (getCommandLength(message) == 4) {
                addTwitchUsername(messageAction);
            }
        } else {
            messageAction = displayHelp(event);
        }
        return messageAction;
    }

    private void addTwitchUsername(MessageAction messageAction) {
        val event = messageAction.getEvent();
        val configOptional = configService.getConfigByServerIdAndName(event.getGuild()
                .getId(), ConfigConstants.STREAMING_ANNOUNCE_USERS);
        val userId = event.getMember().getId();
        val serverId = event.getGuild().getId();
        if (configOptional.isEmpty()) {
            configService.addConfig(serverId, ConfigConstants.STREAMING_ANNOUNCE_USERS, userId);
        }
        val streamingMemberOptional = streamingMemberRepository.findByUserIdAndGuildId(userId, serverId);
        val twitchUsername = getSubcommandNoTransform(event.getMessage().getContentStripped(), 3);
        StreamingMember streamingMember = streamingMemberOptional.orElseGet(() -> new StreamingMember(userId,
                serverId));
        streamingMember.setTwitchUsername(twitchUsername);
        streamingMemberRepository.save(streamingMember);
        messageAction.addSuccessfulReaction();
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
