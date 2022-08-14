package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.common.config.db.ConfigConstants;
import dev.nincodedo.ninbot.common.config.db.ConfigService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentService;
import dev.nincodedo.ninbot.common.config.db.component.ComponentType;
import dev.nincodedo.ninbot.common.logging.FormatLogObject;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Component
public class StreamListener extends StatAwareListenerAdapter {

    private StreamMessageBuilder streamMessageBuilder;
    private ConfigService configService;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private String componentName;

    public StreamListener(ConfigService configService, ComponentService componentService,
            StreamingMemberRepository streamingMemberRepository, StatManager statManager,
            StreamMessageBuilder streamMessageBuilder) {
        super(statManager);
        this.configService = configService;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.streamMessageBuilder = streamMessageBuilder;
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
            var user = member.getUser();
            log.trace("User {} has started streaming in server {}",
                    FormatLogObject.userInfo(user), FormatLogObject.guildName(guild));
            StreamingMember streamingMember =
                    streamingMemberRepository.findByUserIdAndGuildId(user.getId(), guildId)
                            .orElseGet(() -> new StreamingMember(user.getId(), guildId));
            //Grab all the values because this query will get cached easier rather than looking for individual IDs
            var streamingAnnounceUser = configService.getValuesByName(guildId,
                    ConfigConstants.STREAMING_ANNOUNCE_USERS);
            if (streamingAnnounceUser.contains(streamingMember.getUserId())) {
                log.trace("User {} started streaming and is configured to announce", FormatLogObject.userInfo(user));
                var optionalCurrentStream = streamingMember.currentStream();
                //if the streaming member does not have a current stream running, add a new one
                if (optionalCurrentStream.isEmpty()) {
                    streamingMember.startNewStream();
                }
                /*if the stream is recent (stream bounced), then keep using this one as the stream has not really
                ended and set the end time to null. if its already null, well its just null again now*/
                else if (isStreamRecent(optionalCurrentStream.get())) {
                    var currentStream = optionalCurrentStream.get();
                    currentStream.setEndTimestamp(null);
                }
                String streamingUrl = getStreamingUrlFromEvent(event);
                setTwitchUserName(streamingMember, streamingUrl);
                streamingMemberRepository.save(streamingMember);
                var currentStreamOptional = streamingMember.currentStream();
                if (currentStreamOptional.isPresent()) {
                    var currentStream = currentStreamOptional.get();
                    if (currentStream.getAnnounceMessageId() == null) {
                        log.trace("No current announcement for user {} stream. Announcing",
                                FormatLogObject.userInfo(user));
                        announceStream(guild, member, streamingUrl, getActivityFromEvent(event), streamingMember);
                    } else {
                        log.trace("Stream of user {} already has an announcement.", FormatLogObject.userInfo(user));
                    }
                    addRole(guild, member);
                } else {
                    log.trace("User {} has no current stream?", FormatLogObject.userInfo(user));
                }
            } else {
                log.trace("User {} started streaming, but was not configured to announce",
                        FormatLogObject.userInfo(user));
            }
        } else if (hasStoppedStreaming(event)) {
            streamingMemberRepository.findByUserIdAndGuildId(member.getUser().getId(), guildId)
                    .ifPresent(streamingMember -> streamingMember.currentStream().ifPresent(streamInstance -> {
                        log.trace("User {} has stopped streaming", FormatLogObject.userInfo(member.getUser()));
                        streamInstance.setEndTimestamp(LocalDateTime.now());
                        streamingMemberRepository.save(streamingMember);
                    }));
            removeRole(guild, member);
        }
    }

    private boolean isStreamRecent(StreamInstance currentStream) {
        return currentStream.getEndTimestamp() == null
                || currentStream.getEndTimestamp() != null && currentStream.getEndTimestamp()
                .isAfter(LocalDateTime.now().minus(5, ChronoUnit.MINUTES));
    }

    private void setTwitchUserName(StreamingMember streamingMember, String streamingUrl) {
        if (streamingUrl != null && streamingUrl.contains("twitch.tv")) {
            streamingMember.setTwitchUsername(streamingUrl.substring(streamingUrl.lastIndexOf('/') + 1));
        }
    }

    private Activity getActivityFromEvent(GenericEvent event) {
        if (event instanceof UserActivityStartEvent userActivityStartEvent) {
            return userActivityStartEvent.getNewActivity();
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
        onStreamEvent(event, event.getGuild(), event.getMember());
    }

    private boolean hasNoStreamingActivity(List<Activity> activities) {
        return activities.stream()
                .noneMatch(activity -> activity != null && Activity.ActivityType.STREAMING == activity.getType());
    }

    private void removeRole(Guild guild, Member member) {
        configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE).ifPresent(roleId -> {
            var streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null && member.getRoles().contains(streamingRole)) {
                guild.removeRoleFromMember(member, streamingRole).queue();
            }
        });
    }

    private void announceStream(Guild guild, Member member, String streamingUrl, Activity activity,
            StreamingMember streamingMember) {
        var guildId = guild.getId();
        var streamingAnnounceChannel = configService.getSingleValueByName(guildId,
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            var guildChannel = guild.getGuildChannelById(streamingAnnounceChannelString);
            if (streamingUrl != null && guildChannel instanceof GuildMessageChannelUnion channelUnion) {
                var channel = channelUnion.asStandardGuildMessageChannel();
                String gameName = null;
                String streamTitle = null;
                if (activity != null && activity.isRich() && activity.asRichPresence().getState() != null) {
                    var richActivity = activity.asRichPresence();
                    gameName = richActivity.getState();
                    streamTitle = richActivity.getDetails();
                    log.trace("Rich activity found, updating game name to {}, was {}", gameName, streamTitle);
                }
                channel.sendMessage(streamMessageBuilder.buildStreamAnnounceMessage(member, streamingUrl,
                                gameName, streamTitle, guild))
                        .queue(message -> {
                            countOneStat(componentName, guild.getId());
                            updateStreamMemberWithMessageId(streamingMember, message.getId());
                        });
                log.trace("Queued stream message for {} to channel {}", FormatLogObject.memberInfo(member),
                        FormatLogObject.channelInfo(channel));
            } else {
                log.trace("Announcement channel or streaming URL was null, not announcing stream for {} on server {}"
                        , FormatLogObject.memberInfo(member), FormatLogObject.guildName(guild));
            }
        });
    }

    private void updateStreamMemberWithMessageId(StreamingMember streamingMember, String messageId) {
        streamingMemberRepository.findByUserIdAndGuildId(streamingMember.getUserId(), streamingMember.getGuildId())
                .flatMap(StreamingMember::currentStream)
                .ifPresent(streamInstance -> {
                    streamInstance.setAnnounceMessageId(messageId);
                    streamingMember.updateCurrentStream(streamInstance);
                    streamingMemberRepository.save(streamingMember);
                });
    }

    private void addRole(Guild guild, Member member) {
        var streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            var streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                log.trace("Adding role {} to {}", FormatLogObject.roleInfo(streamingRole),
                        FormatLogObject.memberInfo(member));
                guild.addRoleToMember(member, streamingRole).queue();
            } else {
                log.trace("Could not add role ID {} for {}", streamingRoleId, FormatLogObject.memberInfo(member));
            }
        });
    }
}
