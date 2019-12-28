package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import com.nincraft.ninbot.components.config.component.ComponentService;
import com.nincraft.ninbot.components.config.component.ComponentType;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.user.UserActivityEndEvent;
import net.dv8tion.jda.api.events.user.UserActivityStartEvent;
import net.dv8tion.jda.api.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Log4j2
public class TwitchListener extends ListenerAdapter {

    private ConfigService configService;
    private LocaleService localeService;
    private ComponentService componentService;
    private StreamingMemberRepository streamingMemberRepository;
    private String componentName;

    public TwitchListener(ConfigService configService, LocaleService localeService,
            ComponentService componentService, StreamingMemberRepository streamingMemberRepository) {
        this.configService = configService;
        this.localeService = localeService;
        this.componentService = componentService;
        this.streamingMemberRepository = streamingMemberRepository;
        this.componentName = "twitch-announce";
        componentService.registerComponent(componentName, ComponentType.LISTENER);
    }

    @Override
    public void onGenericUserPresence(GenericUserPresenceEvent event) {
        if (componentService.isDisabled(componentName, event.getGuild().getId())) {
            return;
        }
        val userId = event.getMember().getId();
        val guildId = event.getGuild().getId();
        if (event instanceof UserActivityStartEvent) {
            val isStreaming = ((UserActivityStartEvent) event).getNewActivity()
                    .getType()
                    .equals(Activity.ActivityType.STREAMING);
            if (!isStreaming) {
                return;
            }
            log.trace("UserActivityStartEvent, userId: {}, guildId: {}", userId, guildId);
            val optionalStreamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, guildId);
            if (optionalStreamingMember.isPresent()) {
                log.trace("Streamer found in DB");
                val streamingMember = optionalStreamingMember.get();
                deleteOldStreams(streamingMember);
            } else {
                log.trace("Streamer not found in DB, creating a new Streaming Member and attempting announcement");
                StreamingMember streamingMember = new StreamingMember(userId, guildId);
                val activityStartEvent = (UserActivityStartEvent) event;
                val streamingAnnounceUser = configService.getValuesByName(activityStartEvent.getGuild()
                        .getId(), ConfigConstants.STREAMING_ANNOUNCE_USERS);

                if (streamingAnnounceUser.contains(streamingMember.getUserId())) {
                    streamingMemberRepository.save(streamingMember);
                    announceStream(activityStartEvent);
                }
            }
        } else if (event instanceof UserActivityEndEvent && hasNoStreamingActivity(event.getMember().getActivities())) {
            removeRole(event.getGuild(), event.getMember());
        }
    }

    private void deleteOldStreams(StreamingMember streamingMember) {
        if (streamingMember.getStarted().isBefore(LocalDateTime.now().minus(6, ChronoUnit.HOURS))) {
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


    private void announceStream(UserActivityStartEvent userActivityStartEvent) {
        val serverId = userActivityStartEvent.getGuild().getId();
        val streamingAnnounceChannel = configService.getSingleValueByName(serverId,
                ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            val guild = userActivityStartEvent.getGuild();
            val channel = guild.getTextChannelById(streamingAnnounceChannelString);
            val username = userActivityStartEvent.getUser().getName();
            val streamingUrl = userActivityStartEvent.getNewActivity().getUrl();
            if (streamingUrl != null) {
                addRole(guild, guild.getMember(userActivityStartEvent.getUser()));
                if (channel != null) {
                    val activity = userActivityStartEvent.getNewActivity();
                    if (activity.isRich() && activity.asRichPresence().getState() != null) {
                        val richActivity = activity.asRichPresence();
                        String gameName = richActivity.getState();
                        String streamTitle = richActivity.getDetails();
                        log.trace("Rich activity found, updating game name to {}, was {}", gameName, streamTitle);
                        channel.sendMessage(buildStreamAnnounceMessage(userActivityStartEvent.getUser()
                                .getAvatarUrl(), username, streamingUrl, gameName, streamTitle, serverId))
                                .queue();
                        log.trace("Queued stream message for {} to channel {}", username, channel.getId());
                    }
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
            String streamingUrl, String gameName, String streamTitle, String serverId) {
        log.trace("Building stream announce message for {} server {}", username, serverId);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", localeService.getLocale(serverId));
        val embedMessage = new EmbedBuilder()
                .setAuthor(String.format(resourceBundle.getString("listener.twitch.announce"), username, gameName,
                        streamingUrl), streamingUrl, avatarUrl)
                .setTitle(streamTitle)
                .setColor(MessageBuilderHelper.getColor(avatarUrl));
        return new MessageBuilder(embedMessage).build();
    }

    private void addRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                log.trace("Adding role {} to {}", streamingRole.getName(), member.getEffectiveName());
                guild.addRoleToMember(member, streamingRole).queue();
            } else {
                log.trace("Could not add role ID {} for {}", roleId, member.getEffectiveName());
            }
        });
    }
}
