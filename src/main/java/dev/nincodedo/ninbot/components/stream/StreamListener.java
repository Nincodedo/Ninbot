package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
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
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class StreamListener extends StatAwareListenerAdapter {

    private StreamMessageBuilder streamMessageBuilder;
    private ConfigService configService;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private String componentName;

    public StreamListener(ConfigService configService, ComponentService componentService,
            StreamingMemberRepository streamingMemberRepository, StatManager statManager) {
        super(statManager);
        this.configService = configService;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.streamMessageBuilder = new StreamMessageBuilder();
        this.componentName = "stream-announce";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onGuildVoiceStream(@NotNull GuildVoiceStreamEvent event) {
        onStreamEvent(event, event.getGuild(), event.getMember());
    }

    public void onStreamEvent(GenericEvent event, Guild guild, Member member) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        var guildId = guild.getId();
        if (hasStartedStreaming(event)) {
            Optional<String> userIdOptional = getUserIdFromEvent(event);
            if (userIdOptional.isPresent()) {
                StreamingMember streamingMember =
                        streamingMemberRepository.findByUserIdAndGuildId(userIdOptional.get(), guildId)
                                .orElseGet(() -> new StreamingMember(userIdOptional.get(), guildId));
                //Grab all the values because this query will get cached easier rather than looking for individual IDs
                var streamingAnnounceUser = configService.getValuesByName(guildId,
                        ConfigConstants.STREAMING_ANNOUNCE_USERS);
                if (streamingAnnounceUser.contains(streamingMember.getUserId())) {
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
                            announceStream(guild, member, streamingUrl, getActivityFromEvent(event), streamingMember);
                        }
                        addRole(guild, member);
                    }
                }
            }
        } else if (hasStoppedStreaming(event)) {
            streamingMemberRepository.findByUserIdAndGuildId(member.getUser().getId(), guildId)
                    .ifPresent(streamingMember -> streamingMember.currentStream().ifPresent(streamInstance -> {
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

    public boolean hasStoppedStreaming(GenericEvent event) {
        if (event instanceof UserActivityEndEvent userActivityEndEvent) {
            return hasNoStreamingActivity(userActivityEndEvent.getMember().getActivities());
        } else {
            return false;
        }
    }

    private Optional<String> getUserIdFromEvent(GenericEvent event) {
        if (event instanceof UserActivityStartEvent startEvent) {
            return Optional.of(startEvent.getMember().getId());
        } else if (event instanceof GuildVoiceStreamEvent guildVoiceStreamEvent) {
            return Optional.of(guildVoiceStreamEvent.getMember().getId());
        } else {
            return Optional.empty();
        }
    }

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
        var streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            var streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                guild.removeRoleFromMember(member, streamingRole).queue();
            }
        });
    }


    private void announceStream(Guild guild, Member member, String streamingUrl, Activity activity,
            StreamingMember streamingMember) {
        var serverId = guild.getId();
        var streamingAnnounceChannel = configService.getSingleValueByName(serverId,
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            var channel = guild.getTextChannelById(streamingAnnounceChannelString);
            var username = member.getEffectiveName();
            if (streamingUrl != null && channel != null) {
                String gameName = null;
                String streamTitle = null;
                if (activity != null && activity.isRich() && activity.asRichPresence().getState() != null) {
                    var richActivity = activity.asRichPresence();
                    gameName = richActivity.getState();
                    streamTitle = richActivity.getDetails();
                    log.trace("Rich activity found, updating game name to {}, was {}", gameName, streamTitle);
                }
                channel.sendMessage(streamMessageBuilder.buildStreamAnnounceMessage(member.getUser()
                                        .getEffectiveAvatarUrl(), username, streamingUrl,
                                gameName, streamTitle, serverId, guild.getLocale()))
                        .queue(message -> {
                            countOneStat(componentName, guild.getId());
                            updateStreamMemberWithMessageId(streamingMember, message.getId());
                        });
                log.trace("Queued stream message for {} to channel {}", username, channel.getId());
            } else {
                log.trace("Announcement channel or streaming URL was null, not announcing stream for {} on server {}"
                        , username, guild.getId());
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
                log.trace("Adding role {} to {}", streamingRole.getName(), member.getId());
                guild.addRoleToMember(member, streamingRole).queue();
            } else {
                log.trace("Could not add role ID {} for {}", roleId, member.getId());
            }
        });
    }
}
