package dev.nincodedo.ninbot.components.stream;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TwitchStreamListener {
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
    }

    private void registerChannels() {
        var list = streamingMemberRepository.findAllByTwitchUsernameIsNotNull()
                .stream()
                .filter(StreamingMember::getAnnounceEnabled)
                .map(StreamingMember::getTwitchUsername)
                .distinct()
                .toList();
        log.trace("Adding {} user(s) to stream event listener", list.size());
        twitchClient.getClientHelper().enableStreamEventListener(list);
    }

    private void streamStarts(ChannelGoLiveEvent channelGoLiveEvent) {
        var streamingMembers = streamingMemberRepository.findAllByTwitchUsername(channelGoLiveEvent.getChannel()
                .getName());
        for (var streamingMember : streamingMembers) {
            if (componentService.isDisabled(componentName, streamingMember.getGuildId())
                    || Boolean.FALSE.equals(streamingMember.getAnnounceEnabled())) {
                continue;
            }
            var currentStream = streamingMember.currentStream();
            //no current stream, do complete setup
            if (currentStream.isEmpty()) {
                streamingMember.startNewStream();
                streamingMemberRepository.save(streamingMember);
                streamAnnouncer.announceStream(streamingMember, channelGoLiveEvent.getStream()
                        .getGameName(), channelGoLiveEvent.getStream().getTitle());
            }
            //current stream, but no announcement made?
            else if (currentStream.get().getAnnounceMessageId() == null) {
                streamAnnouncer.announceStream(streamingMember, channelGoLiveEvent.getStream()
                        .getGameName(), channelGoLiveEvent.getStream().getTitle());
            }
        }
    }
}
