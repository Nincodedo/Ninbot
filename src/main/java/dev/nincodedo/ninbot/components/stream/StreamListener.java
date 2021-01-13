package dev.nincodedo.ninbot.components.stream;

import dev.nincodedo.ninbot.components.common.LocaleService;
import dev.nincodedo.ninbot.components.common.message.MessageBuilderHelper;
import dev.nincodedo.ninbot.components.common.StatAwareListenerAdapter;
import dev.nincodedo.ninbot.components.config.ConfigConstants;
import dev.nincodedo.ninbot.components.config.ConfigService;
import dev.nincodedo.ninbot.components.config.component.ComponentService;
import dev.nincodedo.ninbot.components.config.component.ComponentType;
import dev.nincodedo.ninbot.components.stats.StatManager;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceStreamEvent;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
@Log4j2
public class StreamListener extends StatAwareListenerAdapter {

    private ConfigService configService;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private String componentName;

    public StreamListener(ConfigService configService, ComponentService componentService,
            StreamingMemberRepository streamingMemberRepository,
            StatManager statManager) {
        super(statManager);
        this.configService = configService;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.componentName = "stream-announce";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onGuildVoiceStream(GuildVoiceStreamEvent event) {
        onStreamEvent(event, event.getGuild(), event.getMember());
    }

    public void onStreamEvent(GenericEvent event, Guild guild, Member member) {
        if (componentService.isDisabled(componentName, guild.getId())) {
            return;
        }
        val guildId = guild.getId();
        if (hasStartedStreaming(event)) {
            Optional<String> userIdOptional = getUserIdFromEvent(event);
            if (userIdOptional.isPresent()) {
                val userId = userIdOptional.get();
                val optionalStreamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, guildId);
                if (optionalStreamingMember.isPresent()) {
                    log.trace("Streamer found in DB");
                    val streamingMember = optionalStreamingMember.get();
                    deleteOldStreams(streamingMember);
                } else {
                    log.trace("Streamer not found in DB, creating a new Streaming Member and attempting announcement");
                    StreamingMember streamingMember = new StreamingMember(userId, guildId);
                    val streamingAnnounceUser = configService.getValuesByName(guildId,
                            ConfigConstants.STREAMING_ANNOUNCE_USERS);

                    if (streamingAnnounceUser.contains(streamingMember.getUserId())) {
                        streamingMemberRepository.save(streamingMember);
                        String streamingUrl = getStreamingUrlFromEvent(event);
                        Activity activity = getActivityFromEvent(event);
                        announceStream(guild, member.getUser(), streamingUrl, activity);
                    }
                }
            }
        } else if (hasStoppedStreaming(event)) {
            removeRole(guild, member);
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
        if (event instanceof UserActivityStartEvent startEvent) {
            return startEvent.getNewActivity().getType().equals(Activity.ActivityType.STREAMING);
        } else if (event instanceof UserActivityEndEvent endEvent) {
            return false;
        } else if (event instanceof GuildVoiceStreamEvent guildVoiceStreamEvent) {
            return guildVoiceStreamEvent.isStream();
        } else {
            return false;
        }
    }

    @Override
    public void onGenericUserPresence(GenericUserPresenceEvent event) {
        onStreamEvent(event, event.getGuild(), event.getMember());
    }

    private void deleteOldStreams(StreamingMember streamingMember) {
        if (streamingMember.getStarted().isBefore(LocalDateTime.now().minus(2, ChronoUnit.HOURS))) {
            log.trace("Old, removing {}", streamingMember.getUserId());
            streamingMemberRepository.delete(streamingMember);
        }
    }

    //twice a day
    @Scheduled(fixedRate = 43200000L)
    private void deleteOldStreams() {
        log.trace("Running scheduled delete of old streams");
        streamingMemberRepository.findAll().forEach(this::deleteOldStreams);
    }

    private boolean hasNoStreamingActivity(List<Activity> activities) {
        return activities.stream()
                .noneMatch(activity -> activity != null && Activity.ActivityType.STREAMING.equals(activity.getType()));
    }

    private void removeRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                guild.removeRoleFromMember(member, streamingRole).queue();
            }
        });
    }


    private void announceStream(Guild guild, User user, String streamingUrl, Activity activity) {
        val serverId = guild.getId();
        val streamingAnnounceChannel = configService.getSingleValueByName(serverId,
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            val channel = guild.getTextChannelById(streamingAnnounceChannelString);
            val username = user.getName();
            if (streamingUrl != null) {
                addRole(guild, guild.getMember(user));
                if (channel != null) {
                    String gameName = null;
                    String streamTitle = null;
                    if (activity != null && activity.isRich() && activity.asRichPresence().getState() != null) {
                        val richActivity = activity.asRichPresence();
                        gameName = richActivity.getState();
                        streamTitle = richActivity.getDetails();
                        log.trace("Rich activity found, updating game name to {}, was {}", gameName, streamTitle);
                    }
                    channel.sendMessage(buildStreamAnnounceMessage(user.getAvatarUrl(), username, streamingUrl,
                            gameName, streamTitle, serverId, guild.getLocale()))
                            .queue(message -> countOneStat(componentName, guild.getId()));
                    log.trace("Queued stream message for {} to channel {}", username, channel.getId());
                } else {
                    log.trace("Announcement channel was null, not announcing stream for {} on server {}", username,
                            guild
                                    .getId());
                }
            } else {
                log.trace("Streaming url was null???");
            }
        });
    }

    Message buildStreamAnnounceMessage(String avatarUrl, String username,
            String streamingUrl, String gameName, String streamTitle, String serverId, Locale locale) {
        log.trace("Building stream announce message for {} server {}", username, serverId);
        ResourceBundle resourceBundle = LocaleService.getResourceBundleOrDefault(locale);
        EmbedBuilder embedBuilder;
        if (!streamingUrl.contains("https://")) {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce.voicechannel"),
                            username, streamingUrl), null, avatarUrl)
                    .setTitle(streamTitle);
        } else {
            embedBuilder = new EmbedBuilder()
                    .setAuthor(String.format(resourceBundle.getString("listener.stream.announce"), username, gameName,
                            streamingUrl), streamingUrl, avatarUrl)
                    .setTitle(streamTitle);
        }
        embedBuilder.setColor(MessageBuilderHelper.getColor(avatarUrl));
        return new MessageBuilder(embedBuilder).build();
    }

    private void addRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                log.trace("Adding role {} to {}", streamingRole.getName(), member.getId());
                guild.addRoleToMember(member, streamingRole).queue();
            } else {
                log.trace("Could not add role ID {} for {}", roleId, member.getId());
            }
        });
    }
}
