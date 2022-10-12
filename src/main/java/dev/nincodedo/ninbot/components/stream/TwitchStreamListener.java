package dev.nincodedo.ninbot.components.stream;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class TwitchStreamListener {
    private StreamMessageBuilder streamMessageBuilder;
    private ConfigService configService;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private TwitchClient twitchClient;
    private String componentName;

    public TwitchStreamListener(StreamMessageBuilder streamMessageBuilder, ConfigService configService,
            ComponentService componentService, StreamingMemberRepository streamingMemberRepository,
            TwitchClient twitchClient) {
        this.streamMessageBuilder = streamMessageBuilder;
        this.configService = configService;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.twitchClient = twitchClient;
        this.componentName = "stream-announce";
        registerChannels();
        registerEvents();
    }

    private void registerEvents() {
        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, this::streamStarts);
    }

    private void registerChannels() {
        var list = streamingMemberRepository.findAll()
                .stream()
                .filter(streamingMember -> configService.getValuesByName(streamingMember.getGuildId(),
                                ConfigConstants.STREAMING_ANNOUNCE_USERS)
                        .contains(streamingMember.getUserId()))
                .map(StreamingMember::getTwitchUsername)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        twitchClient.getClientHelper().enableStreamEventListener(list);
    }

    private void streamStarts(ChannelGoLiveEvent channelGoLiveEvent) {
        var streamingMembers = streamingMemberRepository.findAllByTwitchUsername(channelGoLiveEvent.getChannel()
                .getName());
        for (var streamingMember : streamingMembers) {
            if (componentService.isDisabled(componentName, streamingMember.getGuildId())
                    || !configService.getValuesByName(streamingMember.getGuildId(),
                            ConfigConstants.STREAMING_ANNOUNCE_USERS)
                    .contains(streamingMember.getUserId())) {
                continue;
            }
            //no current stream, do complete setup
            if (streamingMember.currentStream().isEmpty()) {
                streamingMember.startNewStream();
                streamingMemberRepository.save(streamingMember);

            }
            //current stream, but no announcement made?
            else if (streamingMember.currentStream().isPresent()
                    && streamingMember.currentStream().get().getAnnounceMessageId() == null) {

            }
        }

    }
}
