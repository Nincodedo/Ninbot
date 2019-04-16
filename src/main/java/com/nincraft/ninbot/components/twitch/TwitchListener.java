package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.common.LocaleService;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
@Log4j2
public class TwitchListener extends ListenerAdapter {

    private ConfigService configService;
    private LocaleService localeService;
    private List<String> cooldownList;

    public TwitchListener(ConfigService configService, LocaleService localeService) {
        this.configService = configService;
        this.localeService = localeService;
        this.cooldownList = new ArrayList<>();
    }

    @Override
    public void onGenericUserPresence(GenericUserPresenceEvent event) {
        if (event instanceof UserUpdateGameEvent) {
            val updateGameEvent = (UserUpdateGameEvent) event;
            log.debug("User Presence Updated: User {}, Old game {}, New game {}", updateGameEvent.getUser(), updateGameEvent.getOldGame(), updateGameEvent.getNewGame());
            if (updateGameEvent.getNewGame() != null) {
                log.debug("New game URL {}", updateGameEvent.getNewGame().getUrl());
            }
            val serverId = event.getGuild().getId();
            val userId = updateGameEvent.getUser().getId();
            String serverUserId = serverId + "-" + userId;
            if (isNowStreaming(updateGameEvent)) {
                val streamingAnnounceUsers = configService.getValuesByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_USERS);
                if (!cooldownList.contains(serverUserId) && streamingAnnounceUsers.contains(userId)) {
                    Timer timer = new Timer();
                    announceStream(updateGameEvent, serverId);
                    cooldownList.add(serverUserId);
                    timer.schedule(new TwitchAnnounceCooldown(serverUserId), Date.from(Instant.now().plus(30, ChronoUnit.MINUTES)));
                }
            } else if (isNoLongerStreaming(updateGameEvent)) {
                removeRole(updateGameEvent.getGuild(), updateGameEvent.getMember());
                cooldownList.remove(serverUserId);
            }
        }
    }

    private void removeRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            guild.getController().removeSingleRoleFromMember(member, streamingRole).queue();
        });
    }

    private boolean isNoLongerStreaming(UserUpdateGameEvent updateGameEvent) {
        return checkStreaming(updateGameEvent.getNewGame(), updateGameEvent.getOldGame());
    }

    private boolean isNowStreaming(UserUpdateGameEvent updateGameEvent) {
        return checkStreaming(updateGameEvent.getOldGame(), updateGameEvent.getNewGame());
    }

    private boolean checkStreaming(Game previousGame, Game nextGame) {
        return (previousGame == null || previousGame.getUrl() == null) && nextGame != null && nextGame.getUrl() != null;
    }

    private void announceStream(UserUpdateGameEvent updateGameEvent, String serverId) {
        val streamingAnnounceChannel = configService.getSingleValueByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            val guild = updateGameEvent.getGuild();
            val channel = guild.getTextChannelById(streamingAnnounceChannelString);
            val user = updateGameEvent.getUser().getName();
            val url = updateGameEvent.getNewGame().getUrl();
            addRole(guild, guild.getMember(updateGameEvent.getUser()));
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang", localeService.getLocale(serverId));
            channel.sendMessage(String.format(resourceBundle.getString("listener.twitch.announce"), user, url)).queue();
        });
    }

    private void addRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            guild.getController().addSingleRoleToMember(member, streamingRole).queue();
        });
    }

    class TwitchAnnounceCooldown extends TimerTask {

        private String serverUserId;

        TwitchAnnounceCooldown(String serverUserId) {
            this.serverUserId = serverUserId;
        }

        @Override
        public void run() {
            cooldownList.remove(serverUserId);
        }
    }
}