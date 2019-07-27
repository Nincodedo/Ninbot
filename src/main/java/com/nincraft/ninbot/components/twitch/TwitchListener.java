package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.common.MessageBuilderHelper;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.Setter;
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
import java.util.*;

@Component
@Log4j2
public class TwitchListener extends ListenerAdapter {

    private ConfigService configService;
    @Setter
    private Set<SlimMember> streamingMembers;
    private LocaleService localeService;
    @Setter
    private List<SlimMember> cooldownList;
    private TwitchAPI twitchAPI;

    public TwitchListener(ConfigService configService, LocaleService localeService, TwitchAPI twitchAPI) {
        this.configService = configService;
        this.localeService = localeService;
        this.twitchAPI = twitchAPI;
        this.streamingMembers = new HashSet<>();
        this.cooldownList = new ArrayList<>();
    }

    @Override
    public void onGenericUserPresence(GenericUserPresenceEvent event) {
        SlimMember member = new SlimMember(event.getMember().getId(), event.getGuild().getId());
        if (event instanceof UserActivityStartEvent) {
            if (!streamingMembers.contains(member)) {
                val activityStartEvent = (UserActivityStartEvent) event;
                val streamingAnnounceUser = configService.getValuesByName(activityStartEvent.getGuild()
                        .getId(), ConfigConstants.STREAMING_ANNOUNCE_USERS);
                val isStreaming = ((UserActivityStartEvent) event).getNewActivity()
                        .getType()
                        .equals(Activity.ActivityType.STREAMING);
                if (!cooldownList.contains(member) && streamingAnnounceUser.contains(member.getUserId())
                        && isStreaming) {
                    Timer timer = new Timer();
                    announceStream(activityStartEvent);
                    streamingMembers.add(member);
                    timer.schedule(new TwitchAnnounceCooldown(member), Date.from(Instant.now()
                            .plus(30, ChronoUnit.MINUTES)));
                }
            }
        } else if (event instanceof UserActivityEndEvent && streamingMembers.contains(member)
                && event.getMember().getActivities().isEmpty()) {
            removeRole(event.getGuild(), event.getMember());
            streamingMembers.remove(member);
            cooldownList.remove(member);
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
        val streamingAnnounceChannel = configService.getSingleValueByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            val guild = userActivityStartEvent.getGuild();
            val channel = guild.getTextChannelById(streamingAnnounceChannelString);
            val user = userActivityStartEvent.getUser().getName();
            val url = userActivityStartEvent.getNewActivity().getUrl();
            if (url != null) {
                addRole(guild, guild.getMember(userActivityStartEvent.getUser()));
                if (channel != null) {
                    val activity = userActivityStartEvent.getNewActivity();
                    String gameName = activity.getName();
                    if (activity.isRich() && activity.asRichPresence().getDetails() != null) {
                        gameName = activity.asRichPresence().getDetails();
                    }
                    channel.sendMessage(buildStreamAnnounceMessage(userActivityStartEvent, user, url, gameName, serverId))
                            .queue();
                }
            }
        });
    }

    private Message buildStreamAnnounceMessage(UserActivityStartEvent userActivityStartEvent, String user,
            String url, String gameName, String serverId) {
        String iconUrl = twitchAPI.getBoxArtUrl(gameName);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", localeService.getLocale(serverId));
        return new MessageBuilder(new EmbedBuilder()
                .setAuthor(String.format(resourceBundle.getString("listener.twitch.announce"), user, gameName, url), userActivityStartEvent
                        .getUser()
                        .getAvatarUrl(), url)
                .setFooter(String.format("Currently streaming %s", gameName), iconUrl)
                .setTitle(String.format("Watch them play %s!", gameName))
                .setColor(MessageBuilderHelper.getColor(userActivityStartEvent.getUser().getAvatarUrl()))
        ).build();
    }

    private void addRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            if (streamingRole != null) {
                guild.addRoleToMember(member, streamingRole).queue();
            }
        });
    }

    class TwitchAnnounceCooldown extends TimerTask {

        private SlimMember slimMember;

        TwitchAnnounceCooldown(SlimMember slimMember) {
            this.slimMember = slimMember;
        }

        @Override
        public void run() {
            cooldownList.remove(slimMember);
        }
    }
}
