package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentType;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Slf4j
@Component
public class DiscordStreamListener extends StatAwareListenerAdapter implements StreamListener {

    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private StreamAnnouncer streamAnnouncer;
    private String componentName;

    public DiscordStreamListener(ComponentService componentService,
            StreamingMemberRepository streamingMemberRepository, StatManager statManager,
            @Qualifier("statCounterThreadPool") ExecutorService executorService, StreamAnnouncer streamAnnouncer) {
        super(statManager, executorService);
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.streamAnnouncer = streamAnnouncer;
        this.componentName = "stream-announce";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onGuildVoiceStream(@NotNull GuildVoiceStreamEvent event) {
        onStreamEvent(event, event.getGuild(), event.getMember());
    }

    private void onStreamEvent(GenericEvent event, Guild guild, Member member) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        var guildId = guild.getId();
        if (hasStartedStreaming(event)) {
            StreamingMember streamingMember =
                    streamingMemberRepository.findByUserIdAndGuildId(member.getId(), guildId)
                            .orElseGet(() -> new StreamingMember(member.getId(), guildId));
            if (isAnnouncementNotNeeded(streamingMember)) {
                return;
            }
            log.trace("{} started streaming and is configured to announce", FormatLogObject.memberInfo(member));
            setupNewStream(streamingMember);
            String streamingUrl = getStreamingUrlFromEvent(event);
            setTwitchUserName(streamingMember, streamingUrl);
            streamingMemberRepository.save(streamingMember);
            var currentStreamOptional = streamingMember.currentStream();
            if (currentStreamOptional.isPresent()) {
                var currentStream = currentStreamOptional.get();
                if (currentStream.getAnnounceMessageId() == null) {
                    log.trace("No current announcement for {} stream. Announcing", FormatLogObject.memberInfo(member));
                    countOneStat(componentName, guildId);
                    streamAnnouncer.announceStream(streamingMember, guild, member, streamingUrl, getActivity(event,
                            member.getActivities()));
                } else {
                    log.trace("Stream of {} already has an announcement.", FormatLogObject.memberInfo(member));
                }
            } else {
                log.trace("{} has no current stream?", FormatLogObject.memberInfo(member));
            }
        } else if (hasStoppedStreaming(event)) {
            streamingMemberRepository.findByUserIdAndGuildId(member.getUser().getId(), guildId)
                    .ifPresent(streamingMember -> streamingMember.currentStream().ifPresent(streamInstance -> {
                        log.trace("{} has stopped streaming", FormatLogObject.memberInfo(member));
                        streamInstance.setEndTimestamp(LocalDateTime.now());
                        streamingMemberRepository.save(streamingMember);
                    }));
            streamAnnouncer.endStream(guild, member);
        }
    }

    private void setTwitchUserName(StreamingMember streamingMember, String streamingUrl) {
        if (streamingUrl != null && streamingUrl.contains("twitch.tv")) {
            streamingMember.setTwitchUsername(streamingUrl.substring(streamingUrl.lastIndexOf('/') + 1));
        }
    }

    private Activity getActivity(GenericEvent event, List<Activity> activities) {
        if (event instanceof UserActivityStartEvent userActivityStartEvent) {
            return userActivityStartEvent.getNewActivity();
        } else if (activities != null && !activities.isEmpty()) {
            return activities.get(0);
        } else {
            return null;
        }
    }

    private String getStreamingUrlFromEvent(GenericEvent event) {
        if (event instanceof UserActivityStartEvent userActivityStartEvent) {
            return userActivityStartEvent.getNewActivity().getUrl();
        } else if (event instanceof GuildVoiceStreamEvent guildVoiceStreamEvent
                && guildVoiceStreamEvent.getVoiceState().getChannel() != null) {
            return guildVoiceStreamEvent.getVoiceState().getChannel().getName();
        } else {
            return null;
        }
    }

    /**
     * If the event is a UserActivityEndEvent, and the user has no streaming activity, then return true.
     *
     * @param event The event that was received.
     * @return boolean that represents if the event indicates a stream has ended
     */
    public boolean hasStoppedStreaming(GenericEvent event) {
        if (event instanceof UserActivityEndEvent userActivityEndEvent) {
            return hasNoStreamingActivity(userActivityEndEvent.getMember().getActivities());
        } else if (event instanceof GuildVoiceStreamEvent guildVoiceStreamEvent) {
            return hasNoStreamingActivity(guildVoiceStreamEvent.getMember().getActivities());
        } else {
            return false;
        }
    }

    /**
     * If the event is a UserActivityStartEvent, check if the new activity is streaming. If the event is a
     * GuildVoiceStreamEvent, check if the event is a stream. If the event is a UserActivityEndEvent, return false.
     * If the event is null or any other event, return false.
     *
     * @param event The event that was received.
     * @return boolean that represents if the event indicates a stream has started
     */
    public boolean hasStartedStreaming(GenericEvent event) {
        return switch (event) {
            case UserActivityStartEvent startEvent -> startEvent.getNewActivity()
                    .getType() == Activity.ActivityType.STREAMING;
            case GuildVoiceStreamEvent guildVoiceStreamEvent -> guildVoiceStreamEvent.isStream();
            case UserActivityEndEvent ignored -> false;
            case null, default -> false;
        };
    }

    @Override
    public void onGenericUserPresence(@NotNull GenericUserPresenceEvent event) {
        if (!(event instanceof UserActivityStartEvent || event instanceof UserActivityEndEvent)) {
            return;
        }
        onStreamEvent(event, event.getGuild(), event.getMember());
    }

    private boolean hasNoStreamingActivity(List<Activity> activities) {
        return activities.stream()
                .noneMatch(activity -> activity != null && Activity.ActivityType.STREAMING == activity.getType());
    }
}
