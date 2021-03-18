package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.command.AbstractCommand;
import dev.nincodedo.ninbot.components.common.Emojis;
import dev.nincodedo.ninbot.components.common.message.MessageAction;
import dev.nincodedo.ninbot.components.config.Config;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
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
        String message = event.getMessage().getContentStripped();
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
        final net.dv8tion.jda.api.events.message.MessageReceivedEvent event = messageAction.getEvent();
        final java.util.Optional<dev.nincodedo.ninbot.components.config.Config> configOptional =
                configService.getConfigByServerIdAndName(event
                        .getGuild()
                        .getId(), ConfigConstants.STREAMING_ANNOUNCE_USERS);
        String userId = event.getMember().getId();
        String serverId = event.getGuild().getId();
        if (configOptional.isEmpty()) {
            configService.addConfig(serverId, ConfigConstants.STREAMING_ANNOUNCE_USERS, userId);
        }
        final java.util.Optional<dev.nincodedo.ninbot.components.stream.StreamingMember> streamingMemberOptional =
                streamingMemberRepository
                        .findByUserIdAndGuildId(userId, serverId);
        String twitchUsername = getSubcommandNoTransform(event.getMessage().getContentStripped(), 3);
        StreamingMember streamingMember = streamingMemberOptional.orElseGet(() -> new StreamingMember(userId,
                serverId));
        streamingMember.setTwitchUsername(twitchUsername);
        streamingMemberRepository.save(streamingMember);
        messageAction.addSuccessfulReaction();
    }

    private void announceToggle(MessageAction messageAction) {
        final net.dv8tion.jda.api.events.message.MessageReceivedEvent event = messageAction.getEvent();
        String userId = event.getAuthor().getId();
        String serverId = event.getGuild().getId();
        String configName = ConfigConstants.STREAMING_ANNOUNCE_USERS;
        final java.util.List<dev.nincodedo.ninbot.components.config.Config> streamingAnnounceUsers =
                configService.getConfigByName(serverId, configName);
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
