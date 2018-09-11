package com.nincraft.ninbot.components.twitch;

import com.nincraft.ninbot.components.common.MessageUtils;
import com.nincraft.ninbot.components.config.ConfigConstants;
import com.nincraft.ninbot.components.config.ConfigService;
import lombok.extern.log4j.Log4j2;
import lombok.val;
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
            log.debug("UserUpdateEvent " + updateGameEvent);
            log.debug("Old game " + updateGameEvent.getOldGame());
            log.debug("New game " + updateGameEvent.getNewGame());
            if (updateGameEvent.getOldGame() != null && updateGameEvent.getNewGame() != null
                    && updateGameEvent.getOldGame().getUrl() == null && updateGameEvent.getNewGame().getUrl() != null) {
                val serverId = event.getGuild().getId();
                val streamingAnnounceUsers = configService.getValuesByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_USERS);
                if (streamingAnnounceUsers.contains(event.getMember().getUser().getId())) {
                    val streamingAnnounceChannel = configService.getSingleValueByName(serverId, ConfigConstants.STREAMING_ANNOUNCE_CHANNEL);
                    if (streamingAnnounceChannel.isPresent()) {
                        val channel = event.getGuild().getTextChannelById(streamingAnnounceChannel.get());
                        val user = updateGameEvent.getUser().getName();
                        val url = updateGameEvent.getNewGame().getUrl();
                        messageUtils.sendMessage(channel, "%s is streaming! Check them out at %s", user, url);
                    }
                }
            }
        }
    }
}
