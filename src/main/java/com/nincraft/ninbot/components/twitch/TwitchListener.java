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
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

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
            val optionalStreamingMember = streamingMemberRepository.findByUserIdAndGuildId(userId, guildId);
            if (!optionalStreamingMember.isPresent()) {
                StreamingMember streamingMember = new StreamingMember(userId, guildId);
                val activityStartEvent = (UserActivityStartEvent) event;
                val streamingAnnounceUser = configService.getValuesByName(activityStartEvent.getGuild()
                        .getId(), ConfigConstants.STREAMING_ANNOUNCE_USERS);
                val isStreaming = ((UserActivityStartEvent) event).getNewActivity()
                        .getType()
                        .equals(Activity.ActivityType.STREAMING);
                if (streamingAnnounceUser.contains(streamingMember.getUserId())
                        && isStreaming) {
                    streamingMemberRepository.save(streamingMember);
                    Timer timer = new Timer();
                    announceStream(activityStartEvent);
                    timer.schedule(new TwitchAnnounceCooldown(streamingMember), Date.from(Instant.now()
                            .plus(30, ChronoUnit.MINUTES)));
                }
            }
        } else if (event instanceof UserActivityEndEvent && event.getMember().getActivities().isEmpty()) {
            removeRole(event.getGuild(), event.getMember());
            streamingMemberRepository.findByUserIdAndGuildId(userId, guildId)
                    .ifPresent(streamingMember -> streamingMemberRepository.delete(streamingMember));
        }
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
                    String gameName = activity.getName();
                    String streamTitle = gameName;
                    if (activity.isRich() && activity.asRichPresence().getDetails() != null) {
                        gameName = activity.asRichPresence().getDetails();
                        log.trace("Rich activity found, updating game name");
                    }
                    channel.sendMessage(buildStreamAnnounceMessage(userActivityStartEvent, username, streamingUrl,
                            gameName, streamTitle, serverId))
                            .queue();
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

    private Message buildStreamAnnounceMessage(UserActivityStartEvent userActivityStartEvent, String username,
            String streamingUrl, String gameName, String streamTitle, String serverId) {
        log.trace("Building stream announce message for {} server {}", username, serverId);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", localeService.getLocale(serverId));
        val embedMessage = new EmbedBuilder()
                .setAuthor(String.format(resourceBundle.getString("listener.twitch.announce"), username, gameName,
                        streamingUrl), streamingUrl, userActivityStartEvent
                        .getUser()
                        .getAvatarUrl())
                .setTitle(streamTitle)
                .setColor(MessageBuilderHelper.getColor(userActivityStartEvent.getUser().getAvatarUrl()));
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

    class TwitchAnnounceCooldown extends TimerTask {

        private StreamingMember streamingMember;

        TwitchAnnounceCooldown(StreamingMember streamingMember) {
            this.streamingMember = streamingMember;
        }

        @Override
        public void run() {
            streamingMemberRepository.delete(streamingMember);
        }
    }
}
