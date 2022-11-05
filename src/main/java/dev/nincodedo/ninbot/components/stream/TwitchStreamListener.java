package dev.nincodedo.ninbot.components.stream;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Slf4j
@Component
public class TwitchStreamListener implements StreamListener {
    private StreamAnnouncer streamAnnouncer;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private TwitchClient twitchClient;
    private ExecutorService executorService;
    private String componentName;

    public TwitchStreamListener(StreamAnnouncer streamAnnouncer, ComponentService componentService,
            StreamingMemberRepository streamingMemberRepository, TwitchClient twitchClient,
            @Qualifier("listenerThreadPool") ExecutorService executorService) {
        this.streamAnnouncer = streamAnnouncer;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.twitchClient = twitchClient;
        this.executorService = executorService;
        this.componentName = "stream-announce";
        registerChannels();
        registerEvents();
    }

    private void registerEvents() {
        twitchClient.getEventManager()
                .onEvent(ChannelGoLiveEvent.class,
                        channelGoLiveEvent -> executorService.submit(() -> streamStarts(channelGoLiveEvent)));
        twitchClient.getEventManager()
                .onEvent(ChannelGoOfflineEvent.class,
                        channelGoOfflineEvent -> executorService.submit(() -> streamEnds(channelGoOfflineEvent)));
    }

    @Scheduled(timeUnit = TimeUnit.HOURS, fixedDelay = 12, initialDelay = 1)
    protected void registerChannels() {
        var allUsers = streamingMemberRepository.findAllByTwitchUsernameIsNotNull();
        var announceEnabledUsers = allUsers.stream()
                .filter(StreamingMember::getAnnounceEnabled)
                .map(StreamingMember::getTwitchUsername)
                .sorted()
                .distinct()
                .toList();
        log.trace("Adding {} user(s) to stream event listener", announceEnabledUsers.size());
        twitchClient.getClientHelper().enableStreamEventListener(announceEnabledUsers);
        var noAnnouncementUsers = allUsers.stream()
                .filter(Predicate.not(StreamingMember::getAnnounceEnabled))
                .map(StreamingMember::getTwitchUsername)
                .sorted()
                .distinct()
                .toList();
        twitchClient.getClientHelper().disableStreamEventListener(noAnnouncementUsers);
    }

    protected void streamStarts(String username, String gameName, String streamTitle) {
        var streamingMembers = streamingMemberRepository.findAllByTwitchUsername(username);
        for (var streamingMember : streamingMembers) {
            if (componentService.isDisabled(componentName, streamingMember.getGuildId())
                    || isAnnouncementNotNeeded(streamingMember)) {
                continue;
            }
            setupNewStream(streamingMember);
            streamingMemberRepository.save(streamingMember);
            var currentStreamOptional = streamingMember.currentStream();
            if (currentStreamOptional.isPresent() && currentStreamOptional.get().getAnnounceMessageId() == null) {
                streamAnnouncer.announceStream(streamingMember, gameName, streamTitle);
            }
        }
    }

    protected void streamStarts(ChannelGoLiveEvent channelGoLiveEvent) {
        var stream = channelGoLiveEvent.getStream();
        streamStarts(channelGoLiveEvent.getChannel().getName(), stream.getGameName(), stream.getTitle());
    }

    protected void streamEnds(ChannelGoOfflineEvent channelGoOfflineEvent) {
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
