package dev.nincodedo.ninbot.components.stream;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Slf4j
@Component
public class TwitchStreamListener implements StreamListener {
    private StreamAnnouncer streamAnnouncer;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private TwitchClient twitchClient;
    private String componentName;

    public TwitchStreamListener(StreamAnnouncer streamAnnouncer, ComponentService componentService,
            StreamingMemberRepository streamingMemberRepository, TwitchClient twitchClient) {
        this.streamAnnouncer = streamAnnouncer;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.twitchClient = twitchClient;
        this.componentName = "stream-announce";
        registerChannels();
        registerEvents();
    }

    private void registerEvents() {
        twitchClient.getEventManager().onEvent(ChannelGoLiveEvent.class, this::streamStarts);
        twitchClient.getEventManager().onEvent(ChannelGoOfflineEvent.class, this::streamEnds);
    }

    protected void registerChannels() {
        var announceEnabledUsers = streamingMemberRepository.findAllByTwitchUsernameIsNotNull()
                .stream()
                .filter(StreamingMember::getAnnounceEnabled)
                .map(StreamingMember::getTwitchUsername)
                .sorted()
                .distinct()
                .toList();
        log.trace("Adding {} user(s) to stream event listener", announceEnabledUsers.size());
        twitchClient.getClientHelper().enableStreamEventListener(announceEnabledUsers);
        var noAnnouncementUsers = streamingMemberRepository.findAllByTwitchUsernameIsNotNull()
                .stream()
                .filter(Predicate.not(StreamingMember::getAnnounceEnabled))
                .map(StreamingMember::getTwitchUsername)
                .sorted()
                .distinct()
                .toList();
        twitchClient.getClientHelper().disableStreamEventListener(noAnnouncementUsers);
    }

    private void streamStarts(ChannelGoLiveEvent channelGoLiveEvent) {
        var streamingMembers = streamingMemberRepository.findAllByTwitchUsername(channelGoLiveEvent.getChannel()
                .getName());
        for (var streamingMember : streamingMembers) {
            if (componentService.isDisabled(componentName, streamingMember.getGuildId())
                    || isAnnouncementNotNeeded(streamingMember)) {
                continue;
            }
            setupNewStream(streamingMember);
            streamingMemberRepository.save(streamingMember);
            var currentStreamOptional = streamingMember.currentStream();
            if (currentStreamOptional.isPresent() && currentStreamOptional.get().getAnnounceMessageId() == null) {
                streamAnnouncer.announceStream(streamingMember, channelGoLiveEvent.getStream()
                        .getGameName(), channelGoLiveEvent.getStream().getTitle());
            }
        }
    }

    private void streamEnds(ChannelGoOfflineEvent channelGoOfflineEvent) {
        var streamingMembers = streamingMemberRepository.findAllByTwitchUsername(channelGoOfflineEvent.getChannel()
                .getName());
        for (var streamingMember : streamingMembers) {
            streamingMember.currentStream().ifPresent(streamInstance -> {
                streamInstance.setEndTimestamp(LocalDateTime.now());
                streamingMemberRepository.save(streamingMember);
            });
            streamAnnouncer.endStream(streamingMember);
        }
    }
}
