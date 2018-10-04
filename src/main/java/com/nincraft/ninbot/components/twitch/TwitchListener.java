package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.user.update.GenericUserPresenceEvent;
import net.dv8tion.jda.core.events.user.update.UserUpdateGameEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class TwitchListener extends ListenerAdapter {

    private ConfigService configService;
    private MessageUtils messageUtils;

    public TwitchListener(ConfigService configService, MessageUtils messageUtils) {
        this.configService = configService;
        this.messageUtils = messageUtils;
    }

    @Override
    public void onGenericUserPresence(GenericUserPresenceEvent event) {
        if (event instanceof UserUpdateGameEvent) {
            val updateGameEvent = (UserUpdateGameEvent) event;
            if (isNowStreaming(updateGameEvent)) {
                val serverId = event.getGuild().getId();
                val streamingAnnounceUsers = configService.getValuesByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_USERS);
                if (streamingAnnounceUsers.contains(event.getMember().getUser().getId())) {
                    announceStream(updateGameEvent, serverId);
                }
            } else if (isNoLongerStreaming(updateGameEvent)) {
                removeRole(updateGameEvent.getGuild(), updateGameEvent.getMember());
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
        return updateGameEvent.getNewGame() == null && updateGameEvent.getOldGame() != null
                && updateGameEvent.getOldGame().getUrl() != null;
    }

    private boolean isNowStreaming(UserUpdateGameEvent updateGameEvent) {
        return updateGameEvent.getOldGame() == null && updateGameEvent.getNewGame() != null
                && updateGameEvent.getNewGame().getUrl() != null;
    }

    private void announceStream(UserUpdateGameEvent updateGameEvent, String serverId) {
        val streamingAnnounceChannel = configService.getSingleValueByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
        streamingAnnounceChannel.ifPresent(streamingAnnounceChannelString -> {
            val guild = updateGameEvent.getGuild();
            val channel = guild.getTextChannelById(streamingAnnounceChannelString);
            val user = updateGameEvent.getUser().getName();
            val url = updateGameEvent.getNewGame().getUrl();
            addRole(guild, guild.getMember(updateGameEvent.getUser()));
            messageUtils.sendMessage(channel, "%s is streaming! Check them out at %s", user, url);
        });
    }

    private void addRole(Guild guild, Member member) {
        val streamingRoleId = configService.getSingleValueByName(guild.getId(), ConfigConstants.STREAMING_ROLE);
        streamingRoleId.ifPresent(roleId -> {
            val streamingRole = guild.getRoleById(roleId);
            guild.getController().addSingleRoleToMember(member, streamingRole).queue();
        });
    }
}
